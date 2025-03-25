package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventPatch;
import ru.practicum.ewm.event.dto.EventSort;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.FieldValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.exception.ParameterValidationException;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.stats.StatsClient;
import ru.practicum.ewm.stats.ViewStatsDto;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@Slf4j
class EventServiceImpl implements EventService {
    private static final LocalDateTime VIEWS_FROM = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime VIEWS_TO = LocalDateTime.of(2100, Month.DECEMBER, 31, 23, 59, 59);

    private final Clock clock;
    private final CategoryService categoryService;
    private final EventRepository repository;
    private final Duration adminTimeout;
    private final Duration userTimeout;

    EventServiceImpl(
            final Clock clock,
            final CategoryService categoryService,
            final StatsClient statsClient,
            final EventRepository repository,
            @Value("${ewm.timeout.admin}") final Duration adminTimeout,
            @Value("${ewm.timeout.user}") final Duration userTimeout
    ) {
        this.clock = clock;
        this.categoryService = categoryService;
        this.repository = repository;
        this.adminTimeout = adminTimeout;
        this.userTimeout = userTimeout;
    }

    @Override
    @Transactional
    public Event add(final Event event) {
        validateEventDate(event.getEventDate(), userTimeout);
        event.setCategory(fetchCategory(event.getCategory()));
        final Event savedEvent = repository.save(event);
        log.info("Added event with id = {}: {}", savedEvent.getId(), savedEvent);
        return savedEvent;
    }

    @Override
    public Event getById(final long id) {
        return repository.findById(id)
                .map(this::fetchConfirmedRequestsAndHits)
                .orElseThrow(() -> new NotFoundException(Event.class, id));
    }

    @Override
    public Event getPublishedById(long id) {
        return repository.findByIdAndState(id, EventState.PUBLISHED)
                .map(this::fetchConfirmedRequestsAndHits)
                .orElseThrow(() -> new NotFoundException(Event.class, id));
    }

    @Override
    public Event getByIdAndUserId(final long id, final long userId) {
        return repository.findByIdAndInitiatorId(id, userId)
                .map(this::fetchConfirmedRequestsAndHits)
                .orElseThrow(() -> new NotFoundException(Event.class, id));
    }

    @Override
    public List<Event> get(final EventFilter filter) {
        if (filter.rangeStart() != null && filter.rangeEnd() != null
                && filter.rangeEnd().isBefore(filter.rangeStart())) {
            throw new ParameterValidationException("rangeEnd", "must be after or equal to 'rangeStart'",
                    filter.rangeEnd());
        }
        final List<BooleanExpression> predicates = new ArrayList<>();
        final QEvent event = new QEvent("event");
        Optional.ofNullable(filter.text()).ifPresent(text -> predicates.add(event.annotation.likeIgnoreCase(text)
                .or(event.description.likeIgnoreCase(text))));
        Optional.ofNullable(filter.users()).ifPresent(users -> predicates.add(event.initiatorId.in(users)));
        Optional.ofNullable(filter.categories()).ifPresent(categories ->
                predicates.add(event.category.id.in(categories)));
        Optional.ofNullable(filter.states()).ifPresent(states -> predicates.add(event.state.in(states)));
        Optional.ofNullable(filter.paid()).ifPresent(paid -> predicates.add(event.paid.eq(paid)));
        Optional.ofNullable(filter.rangeStart()).ifPresent(start -> predicates.add(event.eventDate.goe(start)));
        Optional.ofNullable(filter.rangeEnd()).ifPresent(end -> predicates.add(event.eventDate.loe(end)));
        final Optional<BooleanExpression> where = predicates.stream().reduce(BooleanExpression::and);
        final Pageable page = PageRequest.of(filter.from() / filter.size(), filter.size());
        final List<Event> events = new ArrayList<>(where.map(w -> repository.findAll(w, page))
                .orElseGet(() -> repository.findAll(page))
                .getContent());
        fetchConfirmedRequestsAndHits(events);

        if (Boolean.TRUE.equals(filter.onlyAvailable())) {
            events.removeIf(foundEvent -> foundEvent.getParticipantLimit() > 0L
                    && foundEvent.getParticipantLimit() - foundEvent.getConfirmedRequests() <= 0L);
        }

        if (filter.sort() == EventSort.EVENT_DATE) {
            events.sort(Comparator.comparing(Event::getEventDate));
        } else if (filter.sort() == EventSort.VIEWS) {
            events.sort(Comparator.comparing(Event::getViews).reversed());
        }

        return events;
    }

    @Override
    @Transactional
    public Event update(final long id, final EventPatch patch) {
        validateEventDate(patch.eventDate(), adminTimeout);
        final Event event = getById(id);
        if (event.getState() != EventState.PENDING) {
            throw new NotPossibleException("Event must be in state PENDING");
        }
        checkPostUpdateEventDate(patch.eventDate(), event.getEventDate(), adminTimeout);
        return updateInternally(event, patch);
    }

    @Override
    @Transactional
    public Event update(final long id, final EventPatch patch, final long userId) {
        validateEventDate(patch.eventDate(), userTimeout);
        final Event event = getByIdAndUserId(id, userId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new NotPossibleException("Only pending or canceled events can be changed");
        }
        checkPostUpdateEventDate(patch.eventDate(), event.getEventDate(), userTimeout);
        return updateInternally(event, patch);
    }

    @Override
    public boolean existsByIdAndUserId(long userId, long eventId) {
        return repository.existsByIdAndInitiatorId(userId, eventId);
    }

    private void validateEventDate(final LocalDateTime eventDate, final Duration timeLimit) {
        if (isFreezeTime(eventDate, timeLimit)) {
            throw new FieldValidationException("eventDate",
                    "must be not earlier than in %s from now".formatted(timeLimit), eventDate);
        }
    }

    private void checkPostUpdateEventDate(final LocalDateTime newEventDate, final LocalDateTime oldEventDate,
            final Duration timeout) {
        if (newEventDate == null && isFreezeTime(oldEventDate, timeout)) {
            throw new NotPossibleException("Event date must be not earlier than in %s from now".formatted(timeout));
        }
    }

    private boolean isFreezeTime(final LocalDateTime dateTime, final Duration timeLimit) {
        return dateTime != null && !Duration.between(now(), dateTime.minus(timeLimit)).isPositive();
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

    private Event updateInternally(final Event event, final EventPatch patch) {
        applyPatch(event, patch);
        if (event.getState() == EventState.PUBLISHED && event.getPublishedOn() == null) {
            event.setPublishedOn(now());
        }
        final Event savedEvent = repository.save(event);
        log.info("updated event with id = {}: {}", savedEvent.getId(), savedEvent);
        return savedEvent;
    }

    private void applyPatch(final Event event, final EventPatch patch) {
        Optional.ofNullable(patch.title()).ifPresent(event::setTitle);
        Optional.ofNullable(patch.category()).map(this::fetchCategory).ifPresent(event::setCategory);
        Optional.ofNullable(patch.eventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(patch.location()).ifPresent(event::setLocation);
        Optional.ofNullable(patch.annotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(patch.description()).ifPresent(event::setDescription);
        Optional.ofNullable(patch.participantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(patch.paid()).ifPresent(event::setPaid);
        Optional.ofNullable(patch.requestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(patch.state()).ifPresent(event::setState);
    }

    private Category fetchCategory(final Category category) {
        if (category == null || category.getId() == null) {
            throw new AssertionError();
        }
        return categoryService.getById(category.getId());
    }
}
