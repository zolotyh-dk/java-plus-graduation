package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.ewm.stats.TestUtils.END;
import static ru.practicum.ewm.stats.TestUtils.ENDPOINT;
import static ru.practicum.ewm.stats.TestUtils.FORMATTER;
import static ru.practicum.ewm.stats.TestUtils.HOST;
import static ru.practicum.ewm.stats.TestUtils.PORT;
import static ru.practicum.ewm.stats.TestUtils.SCHEMA;
import static ru.practicum.ewm.stats.TestUtils.START;
import static ru.practicum.ewm.stats.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestUtils.loadJson;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHitDto;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStatsDto;

@RestClientTest
@ContextConfiguration(classes = StatsClientImpl.class)
@TestPropertySource(properties = "stats.server.uri=http://localhost:9090")
class StatsClientImplIT {

    private static final LogListener logListener = new LogListener(StatsClientImpl.class);

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private StatsClientImpl client;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        mockServer.verify();
    }

    @Test
    void testSaveHit() throws Exception {
        final String requestBody = loadJson("save_hit.json", getClass());
        final EndpointHitDto dto = makeTestEndpointHitDto();
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.CREATED));

        client.saveHit(dto);

        assertLogs(logListener.getEvents(), "logs/save_hit.json", getClass());
    }

    @Test
    void testSaveHitWhen4xxError() throws Exception {
        final String requestBody = loadJson("save_hit.json", getClass());
        final EndpointHitDto dto = makeTestEndpointHitDto();
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        client.saveHit(dto);

        assertLogs(logListener.getEvents(), "logs/save_hit_bad_request.json", getClass());
    }

    @Test
    void testSaveHitWhen5xxError() throws Exception {
        final String requestBody = loadJson("save_hit.json", getClass());
        final EndpointHitDto dto = makeTestEndpointHitDto();
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        client.saveHit(dto);

        assertLogs(logListener.getEvents(), "logs/save_hit_internal_server_error.json", getClass());
    }

    @Test
    void testGetStats() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> actual = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats.json", getClass());
    }

    @Test
    void testGetStatsWhenUrisNull() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(null, false)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> actual = client.getStats(START, END, null, false);

        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_uris_null.json", getClass());
    }

    @Test
    void testGetStatsWhenUrisEmpty() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(), false)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> actual = client.getStats(START, END, List.of(), false);

        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_uris_empty.json", getClass());
    }

    @Test
    void testGetStatsWhenUniqueTrue() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), true)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> actual = client.getStats(START, END, List.of(ENDPOINT), true);

        assertThat(actual, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_unique_true.json", getClass());
    }

    @Test
    void testGetStatsWhen4xxError() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        final List<ViewStatsDto> actual = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(actual, empty());
        assertLogs(logListener.getEvents(), "logs/get_stats_bad_request.json", getClass());
    }

    @Test
    void testGetStatsWhen5xxError() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final List<ViewStatsDto> actual = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(actual, empty());
        assertLogs(logListener.getEvents(), "logs/get_stats_internal_server_error.json", getClass());
    }

    private String hitUri() {
        return UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(PORT)
                .path("/hit")
                .build()
                .encode()
                .toUriString();
    }

    private String statsUri(final List<String> uris, final boolean unique) {
        return UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(PORT)
                .path("/stats")
                .queryParam("start", START.format(FORMATTER))
                .queryParam("end", END.format(FORMATTER))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .encode()
                .toUriString();
    }
}