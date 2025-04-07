package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.SimilarityMapper;
import ru.practicum.ewm.model.Similarity;
import ru.practicum.ewm.model.Weight;
import ru.practicum.ewm.repository.SimilarityRepository;
import ru.practicum.ewm.repository.WeightRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.message.RecommendedEventProto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSimilarityService {
    private static final int NEIGHBOR_QUANTITY = 5;

    private final SimilarityMapper similarityMapper;
    private final SimilarityRepository similarityRepository;
    private final WeightRepository weightRepository;

    public void process(EventSimilarityAvro eventSimilarityAvro) {
        Similarity newSimilarity = similarityMapper.mapToSimilarity(eventSimilarityAvro);
        updateOrCreateSimilarity(newSimilarity);
    }

    private void updateOrCreateSimilarity(Similarity newSimilarity) {
        similarityRepository.findByEventAIdAndEventBId(newSimilarity.getEventAId(), newSimilarity.getEventBId()
        ).ifPresentOrElse(
                existingSimilarity -> updateSimilarity(existingSimilarity, newSimilarity),
                () -> saveNewSimilarity(newSimilarity)
        );
    }

    private void updateSimilarity(Similarity existingSimilarity, Similarity newSimilarity) {
        log.info("Updating similarity: eventAId: {}, eventBId: {}, oldSimilarity: {}, newSimilarity: {}",
                existingSimilarity.getEventAId(),
                existingSimilarity.getEventBId(),
                existingSimilarity.getSimilarity(),
                newSimilarity.getSimilarity());
        existingSimilarity.setSimilarity(newSimilarity.getSimilarity());
        existingSimilarity.setTimestamp(newSimilarity.getTimestamp());
        similarityRepository.save(existingSimilarity);
    }

    private void saveNewSimilarity(Similarity newSimilarity) {
        log.info("Saving new similarity: eventAId: {}, eventBId: {}, similarity: {}",
                newSimilarity.getEventAId(),
                newSimilarity.getEventBId(),
                newSimilarity.getSimilarity());
        similarityRepository.save(newSimilarity);
    }

    public List<RecommendedEventProto> getSimilarEvents(long userId, long eventId, int maxResults) {
        List<Similarity> similarities = similarityRepository.findAllByEventId(eventId);
        log.debug("Fetched {} similarities for eventId: {}", similarities.size(), eventId);

        List<Similarity> filteredOrderedAndLimited = excludeBothEventInteractedPairs(userId, similarities, maxResults);
        return filteredOrderedAndLimited.stream()
                .map(similarity -> RecommendedEventProto.newBuilder()
                        .setEventId(similarity.getEventAId() == eventId ? similarity.getEventBId() : similarity.getEventAId())
                        .setScore(similarity.getSimilarity())
                        .build())
                .toList();
    }


    public List<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        /*
        1. Выгрузить мероприятия, с которыми пользователь уже взаимодействовал.
        При этом отсортировать их по дате взаимодействия от новых к старым и ограничить N взаимодействиями.
        Если пользователь ещё не взаимодействовал ни с одним мероприятием,
        то рекомендовать нечего — возвращается пустой список.
         */
        List<Long> recentlyInteractedEvents = weightRepository.findRecentlyInteractedEventIds(userId, maxResults);
        log.debug("User {} recently interacted with {} events: {}", userId, recentlyInteractedEvents.size(), recentlyInteractedEvents);
        if (recentlyInteractedEvents.isEmpty()) {
            log.debug("No recently interacted events found for user {}", userId);
            return Collections.emptyList();
        }

        /*
        2. Найти похожие новые.
         Найти мероприятия, похожие на те, что отобрали на предыдущем этапе
         */
        List<Similarity> similarToInteracted = similarityRepository.findAllByEventIdIn(recentlyInteractedEvents);
        log.debug("Fetched {} similarities for eventIds: {}", similarToInteracted.size(), recentlyInteractedEvents);

        /* 3. Отобрать те с которыми пользователь не взаимодействовал.
         Отсортировать найденные мероприятия по коэффициенту подобия от большего к меньшему.
         Выбрать из них первые N мероприятий
         */
        List<Long> newEventCandidates = excludeBothEventInteractedPairs(userId, similarToInteracted, maxResults)
                .stream()
                .map(sim -> {
                    long eventAId = sim.getEventAId();
                    long eventBId = sim.getEventBId();
                    return recentlyInteractedEvents.contains(eventAId) ? eventBId : eventAId;
                })
                .toList();
        log.debug("New event candidates for user {}: {}", userId, newEventCandidates);

        /* 4. Найти K ближайших соседей.
        Выгрузить K просмотренных мероприятий, максимально похожих на предсказываемое.
        Важно найти именно максимально похожие мероприятия, с которыми пользователь уже взаимодействовал.
        На основе их оценок и будет предсказываться новая.
         */
        Set<Long> interactedEventIds = weightRepository.findAllEventIdByUserId(userId);
        log.debug("User {} has interacted with {} events: {}", userId, interactedEventIds.size(), interactedEventIds);

        List<Similarity> similarToNewCandidates = similarityRepository
                .findSimilaritiesBetweenNewAndInteracted(newEventCandidates, recentlyInteractedEvents);
        log.debug("Found {} similarities between new events and interacted events", similarToNewCandidates.size());

        Map<Long, List<Neighbor>> newToSimilarInteractedMap = new HashMap<>();
        for (Similarity s : similarToNewCandidates) {
            long newEventId = newEventCandidates.contains(s.getEventAId()) ? s.getEventAId() : s.getEventBId();
            long interactedId = newEventId == s.getEventAId() ? s.getEventBId() : s.getEventAId();
            newToSimilarInteractedMap
                    .computeIfAbsent(newEventId, id -> new ArrayList<>())
                    .add(new Neighbor(interactedId, s.getSimilarity()));
        }

        /* 5. Получить оценки.
         Для всех мероприятий, полученных на предыдущем этапе, выгрузить оценки, которые поставил пользователь.
         */
        List<Weight> weights = weightRepository.findByUserIdAndEventIdIn(userId, interactedEventIds);
        log.debug("Fetched {} weights for user {} interacted events: {}", weights.size(), userId, weights);
        Map<Long, Double> weightsMap = weights.stream()
                .collect(Collectors.toMap(Weight::getEventId, Weight::getWeight));

        /* 6. Вычислить сумму взвешенных оценок.
         Используя коэффициенты подобия, полученные на шаге 4, и оценки, полученные на шаге 5,
         вычислить сумму взвешенных оценок (перемножить оценки мероприятий с их коэффициентами подобия,
         все полученные произведения сложить).
         */
        List<PredictedScore> predictedScores = new ArrayList<>();
        for (Map.Entry<Long, List<Neighbor>> entry : newToSimilarInteractedMap.entrySet()) {
            long newEventId = entry.getKey();
            List<Neighbor> neighbors = entry.getValue().stream()
                    .filter(neighbor -> weightsMap.containsKey(neighbor.eventId()))
                    .sorted(Comparator.comparingDouble(Neighbor::similarity).reversed())
                    .limit(NEIGHBOR_QUANTITY)
                    .toList();

            double numerator = 0;
            double denominator = 0;
            for (Neighbor neighbor : neighbors) {
                /* 7. Вычислить сумму коэффициентов подобия.
                Сложить все коэффициенты подобия, полученные на шаге 4.
                */
                double similarity = neighbor.similarity();
                denominator += similarity;
                double weight = weightsMap.get(neighbor.eventId());
                numerator += weight * similarity;
            }

            /* 8. Вычислить оценку нового мероприятия.
            Поделить сумму взвешенных оценок на сумму коэффициентов.
             */
            if (denominator > 0) {
                double predicted = numerator / denominator;
                log.debug("Predicted score for newEventId {} = {}", newEventId, predicted);
                predictedScores.add(new PredictedScore(newEventId, predicted));
            } else {
                log.debug("Skipping prediction for newEventId {} due to zero denominator", newEventId);
            }
        }
        log.debug("Predicted scores for user {}: {}", userId, predictedScores);
        return predictedScores.stream()
                .sorted(Comparator.comparingDouble(PredictedScore::score).reversed())
                .limit(maxResults)
                .map(ps -> RecommendedEventProto.newBuilder()
                        .setEventId(ps.eventId())
                        .setScore(ps.score())
                        .build())
                .toList();
    }


    private List<Similarity> excludeBothEventInteractedPairs(long userId, List<Similarity> similarities, int maxResults) {
        Set<Long> interactedEventIds = weightRepository.findAllEventIdByUserId(userId);
        log.debug("User with id {} interacted with {} events", userId, interactedEventIds.size());
        List<Similarity> filteredOrderedAndLimited = similarities.stream()
                .filter(similarity -> {
                    boolean interactedWithEventA = interactedEventIds.contains(similarity.getEventAId());
                    boolean interactedWithEventB = interactedEventIds.contains(similarity.getEventBId());
                    return !(interactedWithEventA && interactedWithEventB);
                })
                .sorted(Comparator.comparing(Similarity::getSimilarity).reversed())
                .limit(maxResults)
                .toList();
        log.debug("Filtered out {} similarities based on user interactions", similarities.size() - filteredOrderedAndLimited.size());
        log.debug("Sorted and limited to {} similarities", filteredOrderedAndLimited.size());
        return filteredOrderedAndLimited;
    }

    private record PredictedScore(
            long eventId,
            double score) {
    }

    private record Neighbor(
            long eventId,
            double similarity) {
    }
}
