package ru.practicum.ewm.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.ewm.stats.TestUtils.deepEqualTo;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHit;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHitDto;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStats;
import static ru.practicum.ewm.stats.TestUtils.makeTestViewStatsDto;

class StatsMapperTest {

    private StatsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StatsMapper();
    }

    @Test
    void testMapToEndpointHit() {

        final EndpointHit actual = mapper.mapToEndpointHit(makeTestEndpointHitDto());

        assertThat(actual, deepEqualTo(makeTestEndpointHit().withNoId()));
    }

    @Test
    void testMapToEndpointHitWhenNull() {

        final EndpointHit actual = mapper.mapToEndpointHit(null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleViewStats() {

        final ViewStatsDto actual = mapper.mapToDto(makeTestViewStats());

        assertThat(actual, equalTo(makeTestViewStatsDto()));
    }

    @Test
    void testMapToDtoWhenSingleViewStatsNull() {

        final ViewStatsDto actual = mapper.mapToDto((ViewStats) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenViewStatsList() {

        final List<ViewStatsDto> actual = mapper.mapToDto(List.of(makeTestViewStats()));

        assertThat(actual, contains(makeTestViewStatsDto()));
    }

    @Test
    void testMapToDtoWhenViewStatsListNull() {

        final List<ViewStatsDto> actual = mapper.mapToDto((List<ViewStats>) null);

        assertThat(actual, nullValue());
    }
}