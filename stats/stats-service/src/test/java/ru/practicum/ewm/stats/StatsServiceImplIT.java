package ru.practicum.ewm.stats;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.stats.TestUtils.makeTestEndpointHit;

@SpringBootTest
class StatsServiceImplIT {

    @MockBean
    private StatsRepository repository;

    @Autowired
    private StatsService service;

    @Test
    void testEndpointHitValidated() {
        final EndpointHit invalidHit = makeTestEndpointHit();
        invalidHit.setApp(null);

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.addEndpointHit(invalidHit));

        final List<String> fieldsWithViolation = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();
        assertThat(fieldsWithViolation, contains("addEndpointHit.endpointHit.app"));
    }
}