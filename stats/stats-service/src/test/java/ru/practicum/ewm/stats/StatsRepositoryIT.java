package ru.practicum.ewm.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static ru.practicum.ewm.stats.TestUtils.APP;
import static ru.practicum.ewm.stats.TestUtils.END;
import static ru.practicum.ewm.stats.TestUtils.ENDPOINT;
import static ru.practicum.ewm.stats.TestUtils.IP;
import static ru.practicum.ewm.stats.TestUtils.START;
import static ru.practicum.ewm.stats.TestUtils.TIMESTAMP;
import static ru.practicum.ewm.stats.TestUtils.equalTo;

@DataJpaTest
class StatsRepositoryIT {

    private static final String ENDPOINT_2 = "endpointB";
    private static final String ENDPOINT_3 = "endpointC";

    @Autowired
    private StatsRepository repository;

    @Test
    void testGetHitsWhenAllEndpoints() {

        final List<ViewStats> actual = repository.getHits(START, END);

        assertThat(actual, contains(
                equalTo(endpointC()),
                equalTo(endpointB()),
                equalTo(endpointA())
        ));
    }

    @Test
    void testGetUniqueHitsWhenAllEndpoints() {

        final List<ViewStats> actual = repository.getUniqueHits(START, END);

        assertThat(actual, contains(
                equalTo(endpointC()),
                equalTo(endpointBUnique()),
                equalTo(endpointA())
        ));
    }

    @Test
    void testGetHitsWhenSelectedEndpoints() {
        final List<String> uris = List.of(ENDPOINT, ENDPOINT_2);

        final List<ViewStats> actual = repository.getHits(START, END, uris);

        assertThat(actual, contains(
                equalTo(endpointB()),
                equalTo(endpointA())
        ));
    }

    @Test
    void testGetUniqueHitsWhenSelectedEndpoints() {
        final List<String> uris = List.of(ENDPOINT, ENDPOINT_2);

        final List<ViewStats> actual = repository.getUniqueHits(START, END, uris);

        assertThat(actual, contains(
                equalTo(endpointBUnique()),
                equalTo(endpointA())
        ));
    }

    @Test
    void testSaveNewEndpointHit() {

        repository.save(newEndpointHit());
        final List<ViewStats> actual = repository.getHits(START, END);

        assertThat(actual, contains(
                equalTo(endpointC()),
                equalTo(endpointB()),
                equalTo(endpointAIncreased())
        ));
    }

    private ViewStats endpointA() {
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
                return 1L;
            }
        };
    }

    private ViewStats endpointAIncreased() {
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
                return 2L;
            }
        };
    }

    private ViewStats endpointB() {
        return new ViewStats() {
            @Override
            public String getApp() {
                return APP;
            }

            @Override
            public String getUri() {
                return ENDPOINT_2;
            }

            @Override
            public Long getHits() {
                return 3L;
            }
        };
    }

    private ViewStats endpointBUnique() {
        return new ViewStats() {
            @Override
            public String getApp() {
                return APP;
            }

            @Override
            public String getUri() {
                return ENDPOINT_2;
            }

            @Override
            public Long getHits() {
                return 2L;
            }
        };
    }

    private ViewStats endpointC() {
        return new ViewStats() {
            @Override
            public String getApp() {
                return APP;
            }

            @Override
            public String getUri() {
                return ENDPOINT_3;
            }

            @Override
            public Long getHits() {
                return 4L;
            }
        };
    }

    private EndpointHit newEndpointHit() {
        final EndpointHit hit = new EndpointHit();
        hit.setApp(APP);
        hit.setUri(ENDPOINT);
        hit.setIp(IP);
        hit.setTimestamp(TIMESTAMP);
        return hit;
    }
}