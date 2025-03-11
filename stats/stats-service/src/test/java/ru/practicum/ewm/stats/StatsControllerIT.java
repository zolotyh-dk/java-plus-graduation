package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.stats.TestUtils.END;
import static ru.practicum.ewm.stats.TestUtils.ENDPOINT;
import static ru.practicum.ewm.stats.TestUtils.START;
import static ru.practicum.ewm.stats.TestUtils.equalTo;
import static ru.practicum.ewm.stats.TestUtils.loadJson;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHit;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHitDto;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStats;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStatsDto;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerIT {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @MockBean
    private StatsService mockService;

    @MockBean
    private StatsMapper mockMapper;

    @Captor
    private ArgumentCaptor<List<ViewStats>> viewStatsCaptor;

    private InOrder inOrder;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockService, mockMapper);
        inOrder = Mockito.inOrder(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void testNewHitReachesEndpoint() throws Exception {
        final String requestBody = loadJson("add_endpoint_hit_request.json", getClass());
        when(mockMapper.mapToEndpointHit(makeTestEndpointHitDto())).thenReturn(makeTestEndpointHit().withNoId());

        mvc.perform(post("/hit")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        inOrder.verify(mockMapper).mapToEndpointHit(makeTestEndpointHitDto());
        inOrder.verify(mockService).addEndpointHit(makeTestEndpointHit().withNoId());
    }

    @Test
    void testEndpointHitDtoValidated() throws Exception {
        final String requestBody = loadJson("add_endpoint_hti_wrong_format_request.json", getClass());
        final String responseBody = loadJson("add_endpoint_hit_wrong_format_response.json", getClass());

        mvc.perform(post("/hit")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void testStatsRequestReachedEndpoint() throws Exception {
        final String responseBody = loadJson("get_view_stats_response.json", getClass());
        when(mockService.getViewStats(START, END, List.of(ENDPOINT), false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get("/stats?start={start}&end={end}&uris={endpoint}&unique=false",
                        START.format(FORMATTER), END.format(FORMATTER), ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testErrorResponseWhenStatsRequestWithoutStartDate() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_start_response.json", getClass());

        mvc.perform(get("/stats?end={end}&uris={endpoint}&unique=false", END.format(FORMATTER), ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void testErrorResponseWhenStatsRequestWithoutEndDate() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_end_response.json", getClass());

        mvc.perform(get("/stats?start={start}&uris={endpoint}&unique=false", START.format(FORMATTER), ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void testStatsRequestWhenUrisEmpty() throws Exception {
        final String responseBody = loadJson("get_view_stats_empty_uris_response.json", getClass());
        when(mockService.getViewStats(START, END, List.of(), false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get("/stats?start={start}&end={end}&uris=&unique=false",
                        START.format(FORMATTER), END.format(FORMATTER))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(), false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testStatsRequestWhenNoUris() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_uris_response.json", getClass());
        when(mockService.getViewStats(START, END, null, false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get("/stats?start={start}&end={end}&unique=false", START.format(FORMATTER), END.format(FORMATTER))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, null, false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testStatsRequestWhenUniqueTrue() throws Exception {
        final String responseBody = loadJson("get_view_stats_unique_true_response.json", getClass());
        when(mockService.getViewStats(START, END, List.of(ENDPOINT), true)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get("/stats?start={start}&end={end}&uris={endpoint}&unique=true",
                        START.format(FORMATTER), END.format(FORMATTER), ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), true);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
    }

    @Test
    void testStatsRequestWhenNoUnique() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_unique_response.json", getClass());
        when(mockService.getViewStats(START, END, List.of(ENDPOINT), false)).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get("/stats?start={start}&end={end}&uris={endpoint}",
                        START.format(FORMATTER), END.format(FORMATTER), ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), false);
        inOrder.verify(mockMapper).mapToDto(viewStatsCaptor.capture());
        assertThat(viewStatsCaptor.getValue(), contains(equalTo(makeTestViewStats())));
    }

    @TestConfiguration
    static class Config {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2000-01-01T00:00:01Z"), ZoneId.of("Z"));
        }
    }
}