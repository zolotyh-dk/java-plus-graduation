package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.stats.TestUtils.END;
import static ru.practicum.ewm.stats.TestUtils.ENDPOINT;
import static ru.practicum.ewm.stats.TestUtils.START;
import static ru.practicum.ewm.stats.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestUtils.equalTo;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHit;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHitDto;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStats;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStatsDto;

class StatsControllerTest {

    private static final LogListener logListener = new LogListener(StatsController.class);

    private static final String METHOD = "POST";
    private static final String URI = "http://somehost/home";
    private static final String QUERY_STRING = "value=none";

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Mock
    private StatsService mockService;

    @Mock
    private StatsMapper mockMapper;

    @Captor
    private ArgumentCaptor<List<ViewStats>> viewStatsCaptor;

    private InOrder inOrder;

    private StatsController controller;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        Mockito.when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        Mockito.when(mockHttpRequest.getRequestURI()).thenReturn(URI);
        Mockito.when(mockHttpRequest.getQueryString()).thenReturn(QUERY_STRING);
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new StatsController(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper, mockHttpRequest);
        openMocks.close();
    }

    @Test
    void testAddEndpointHit() throws JSONException, IOException {
        when(mockMapper.mapToEndpointHit(makeTestEndpointHitDto())).thenReturn(makeTestEndpointHit().withNoId());

        controller.addEndpointHit(makeTestEndpointHitDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToEndpointHit(makeTestEndpointHitDto());
        inOrder.verify(mockService).addEndpointHit(makeTestEndpointHit().withNoId());
        assertLogs(logListener.getEvents(), "add_endpoint_hit.json", getClass());
    }

    @Test
    void testGetViewStats() throws JSONException, IOException {
        when(mockService.getViewStats(START, END, List.of(ENDPOINT), false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        final List<ViewStatsDto> actual = controller.getViewStats(START, END, List.of(ENDPOINT), false,
                mockHttpRequest);

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "get_view_stats.json", getClass());
    }

    @Test
    void testGetViewStatsWhenNoUris() throws JSONException, IOException {
        when(mockService.getViewStats(START, END, null, false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        final List<ViewStatsDto> actual = controller.getViewStats(START, END, null, false, mockHttpRequest);

        inOrder.verify(mockService).getViewStats(START, END, null, false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "get_view_stats_no_uris.json", getClass());
    }
}