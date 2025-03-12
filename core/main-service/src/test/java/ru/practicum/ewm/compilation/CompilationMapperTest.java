package ru.practicum.ewm.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.compilation.CompilationTestUtil.*;
import static ru.practicum.ewm.event.EventTestUtil.EVENT_SHORT_DTO_1;

class CompilationMapperTest {

    private CompilationMapper compilationMapper;
    private EventMapper eventMapper;
    private Event event;

    @BeforeEach
    void setUp() {
        eventMapper = mock(EventMapper.class);
        compilationMapper = new CompilationMapper(eventMapper);
        event = new Event();
        when(eventMapper.mapToDto(event)).thenReturn(EVENT_SHORT_DTO_1);
    }

    @Test
    void shouldMapToCompilation() {
        Set<Event> events = Set.of(event);

        Compilation result = compilationMapper.mapToCompilation(NEW_COMPILATION_DTO, events);

        assertThat(result, is(notNullValue()));
        assertThat(result.getTitle(), is(COMPILATION_TITLE_2));
        assertThat(result.getPinned(), is(true));
        assertThat(result.getEvents(), hasSize(1));
        assertThat(result.getEvents(), containsInAnyOrder(event));
    }

    @Test
    void shouldMapToDto() {
        Compilation compilation = new Compilation();
        compilation.setId(COMPILATION_ID_1);
        compilation.setTitle(COMPILATION_TITLE_1);
        compilation.setPinned(true);
        compilation.setEvents(Set.of(event));

        CompilationDto result = compilationMapper.mapToDto(compilation);

        assertThat(result, is(notNullValue()));
        assertThat(result.id(), is(COMPILATION_ID_1));
        assertThat(result.title(), is(COMPILATION_TITLE_1));
        assertThat(result.pinned(), is(true));
        assertThat(result.events(), hasSize(1));
        assertThat(result.events(), containsInAnyOrder(EVENT_SHORT_DTO_1));
    }

    @Test
    void shouldMapToDtoList() {
        Compilation compilation1 = new Compilation();
        compilation1.setId(COMPILATION_ID_1);
        compilation1.setTitle(COMPILATION_TITLE_1);
        compilation1.setPinned(true);
        compilation1.setEvents(Set.of(event));

        Compilation compilation2 = new Compilation();
        compilation2.setId(COMPILATION_ID_2);
        compilation2.setTitle(COMPILATION_TITLE_2);
        compilation2.setPinned(false);
        compilation2.setEvents(Set.of(event));

        List<CompilationDto> result = compilationMapper.mapToDto(List.of(compilation1, compilation2));

        assertThat(result, hasSize(2));
        assertThat(result.get(0).id(), is(COMPILATION_ID_1));
        assertThat(result.get(1).id(), is(COMPILATION_ID_2));
        assertThat(result.get(0).title(), is(COMPILATION_TITLE_1));
        assertThat(result.get(1).title(), is(COMPILATION_TITLE_2));
    }

    @Test
    void shouldReturnNullWhenMappingNullCompilationToDto() {
        assertThat(compilationMapper.mapToDto((Compilation) null), is(nullValue()));
    }

    @Test
    void shouldReturnNullWhenMappingNullDtoListToDto() {
        assertThat(compilationMapper.mapToDto((List<Compilation>) null), is(nullValue()));
    }
}