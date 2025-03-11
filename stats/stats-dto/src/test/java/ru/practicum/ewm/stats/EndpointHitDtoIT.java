package ru.practicum.ewm.stats;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

class EndpointHitDtoIT {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldNotViolateConstraintWhenCorrectDto() {

        final Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(makeTestEndpointHitDto());

        assertThat(violations, empty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintWhenAppNullOrBlank(final String app) {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .app(app)
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("app"));
    }

    @Test
    void shouldViolateConstraintWhenAppLengthExceeds255() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .app("a".repeat(256))
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("app"));
    }

    @Test
    void shouldNotViolateConstraintWhenAppLength255() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .app("a".repeat(255))
                .build();

        final Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintWhenUriNullOrBlank(final String uri) {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .uri(uri)
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("uri"));
    }

    @Test
    void shouldViolateConstraintWhenUriLengthExceeds512() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .uri("a".repeat(513))
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("uri"));
    }

    @Test
    void shouldNotViolateConstraintWhenUriLength512() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .uri("a".repeat(512))
                .build();

        final Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintWhenIpNullOrBlank(final String ip) {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .ip(ip)
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("ip"));
    }

    @Test
    void shouldViolateConstraintWhenIpLengthExceeds40() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .ip("a".repeat(41))
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("ip"));
    }

    @Test
    void shouldNotViolateConstraintWhenIpLength40() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .ip("a".repeat(40))
                .build();

        final Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    @Test
    void shouldViolateConstraintWhenTimestampNull() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .timestamp(null)
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("timestamp"));
    }

    @Test
    void shouldViolateConstraintWhenTimestampInFuture() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(2L))
                .build();

        final List<String> fieldsWithViolation = validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertThat(fieldsWithViolation, contains("timestamp"));
    }

    @Test
    void shouldNotViolateConstraintWhenTimestampNow() {
        final EndpointHitDto dto = makeTestEndpointHitDto().toBuilder()
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        final Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    private EndpointHitDto makeTestEndpointHitDto() {
        return EndpointHitDto.builder()
                .app("mainService")
                .uri("endpointA")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.of(2000, Month.JANUARY, 31, 13, 30, 55))
                .build();
    }
}