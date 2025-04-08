package ru.practicum.ewm.model;

/**
 * Событие, рекомендуемое пользователю
 *
 * @param eventId идентификатор рекомендуемого мероприятия
 * @param score   либо предсказанная оценка, либо коэффициент сходства, либо сумма весов действий с мероприятием
 */
public record RecommendedEvent(
        long eventId,
        double score
) {
}
