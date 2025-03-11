package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;

final class TestUtils {

    static final long ENDPOINT_HIT_ID = 1L;
    static final String APP = "mainService";
    static final String ENDPOINT = "endpointA";
    static final String IP = "127.0.0.1";
    static final long HITS = 99L;
    static final LocalDateTime TIMESTAMP = LocalDateTime.of(2000, Month.JANUARY, 31, 13, 30, 55);
    static final LocalDateTime START = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 1);
    static final LocalDateTime END = LocalDateTime.of(2000, Month.FEBRUARY, 2, 0, 0, 2);

    private static final ObjectMapper mapper = new ObjectMapper();

    private TestUtils() {
    }

    static EndpointHitDto makeTestEndpointHitDto() {
        return EndpointHitDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .ip(IP)
                .timestamp(TIMESTAMP)
                .build();
    }

    static ViewStatsDto makeTestViewStatsDto() {
        return ViewStatsDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .hits(HITS)
                .build();
    }

    static EndpointHitProxy makeTestEndpointHit() {
        final EndpointHitProxy endpointHit = new EndpointHitProxy();
        endpointHit.setId(ENDPOINT_HIT_ID);
        endpointHit.setApp(APP);
        endpointHit.setUri(ENDPOINT);
        endpointHit.setIp(IP);
        endpointHit.setTimestamp(TIMESTAMP);
        return endpointHit;
    }

    static ViewStats makeTestViewStats() {
        return new ViewStats() {
            @Override
            public String getApp() {
                return APP;
            }

            @Override
            public String getUri() {
                return ENDPOINT;
            }

            @Override
            public Long getHits() {
                return HITS;
            }
        };
    }

    static String loadJson(final String filename, final Class<?> clazz) throws IOException {
        final String expandedFilename = clazz.getSimpleName().toLowerCase() + "/" + filename;
        final ClassPathResource resource = new ClassPathResource(expandedFilename, clazz);
        return Files.readString(resource.getFile().toPath());
    }

    static void assertLogs(final List<LogListener.Event> events, final String filename,
            final Class<?> clazz) throws IOException, JSONException {
        final String expected = loadJson(filename, clazz);
        final String actual = mapper.writeValueAsString(events);
        JSONAssert.assertEquals(expected, actual, false);
    }

    static <T extends EndpointHit> Matcher<T> deepEqualTo(final EndpointHitProxy endpointHit) {
        return new TypeSafeMatcher<>() {

            private final EndpointHitProxy expected = endpointHit;

            @Override
            protected boolean matchesSafely(final T actual) {
                return expected.equals(actual);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<ViewStats> equalTo(final ViewStats viewStats) {
        return new TypeSafeMatcher<>() {

            private final ViewStats expected = viewStats;

            @Override
            protected boolean matchesSafely(final ViewStats actual) {
                return Objects.equals(expected.getApp(), actual.getApp())
                        && Objects.equals(expected.getUri(), actual.getUri())
                        && Objects.equals(expected.getHits(), actual.getHits());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("app=%s, uri=%s, hits=%s"
                        .formatted(expected.getApp(), expected.getUri(), expected.getHits()));
            }
        };
    }
}
