package ru.practicum.ewm.stats;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.stats.TestUtils.END;
import static ru.practicum.ewm.stats.TestUtils.ENDPOINT;
import static ru.practicum.ewm.stats.TestUtils.START;
import static ru.practicum.ewm.stats.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestUtils.equalTo;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHit;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStats;

class StatsServiceImplTest {

    private static final LogListener logListener = new LogListener(StatsServiceImpl.class);

    private AutoCloseable openMocks;

    @Mock
    private StatsRepository repository;

    private StatsService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new StatsServiceImpl(repository);
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(repository);
        openMocks.close();
    }

    @Test
    void testAddEndpointHit() throws JSONException, IOException {
        when(repository.save(makeTestEndpointHit().withNoId())).thenReturn(makeTestEndpointHit());

        service.addEndpointHit(makeTestEndpointHit().withNoId());

        verify(repository).save(makeTestEndpointHit().withNoId());
        assertLogs(logListener.getEvents(), "add_endpoint_hit.json", getClass());
    }

    @Test
    void testAddEndpointHitWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.addEndpointHit(null));

        assertThat(exception.getMessage(), equalTo("Cannot add endpoint hit: is null"));
    }

    @Test
    void testGetViewStatsWhenUrisNull() {
        when(repository.getHits(START, END)).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> actual = service.getViewStats(START, END, null, false);

        verify(repository).getHits(START, END);
        assertThat(actual, contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testGetViewStatsWhenNoUris() {
        when(repository.getHits(START, END)).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> actual = service.getViewStats(START, END, List.of(), false);

        verify(repository).getHits(START, END);
        assertThat(actual, contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testGetViewStatsWhenNoUrisAndUnique() {
        when(repository.getUniqueHits(START, END)).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> actual = service.getViewStats(START, END, null, true);

        verify(repository).getUniqueHits(START, END);
        assertThat(actual, contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testGetViewStatsWhenUris() {
        when(repository.getHits(START, END, List.of(ENDPOINT))).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> actual = service.getViewStats(START, END, List.of(ENDPOINT), false);

        verify(repository).getHits(START, END, List.of(ENDPOINT));
        assertThat(actual, contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testGetViewStatsWhenUrisAndUnique() {
        when(repository.getUniqueHits(START, END, List.of(ENDPOINT))).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> actual = service.getViewStats(START, END, List.of(ENDPOINT), true);

        verify(repository).getUniqueHits(START, END, List.of(ENDPOINT));
        assertThat(actual, contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testGetViewStatsWhenStartNull() {

        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.getViewStats(null, END, null, false));

        assertThat(exception.getMessage(), equalTo("Date and time to gather stats from cannot be null"));
    }

    @Test
    void testGetViewStatsWhenEndNull() {

        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.getViewStats(START, null, null, false));

        assertThat(exception.getMessage(), equalTo("Date and time to gather stats to cannot be null"));
    }
}
