package ru.practicum.ewm.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class NotFoundException extends RuntimeException {

    private final String modelName;
    private final Set<Long> modelIds;

    public NotFoundException(final String modelName, final Long modelId) {
        super("%s with id = %s not found".formatted(modelName, modelId));
        this.modelName = modelName;
        this.modelIds = Set.of(modelId);
    }

    public <T> NotFoundException(final Class<T> modelClass, final Long modelId) {
        this(modelClass.getSimpleName(), modelId);
    }

    public NotFoundException(final String modelName, final Set<Long> modelIds) {
        super("%s with ids = %s not found".formatted(modelName, modelIds));
        this.modelName = modelName;
        this.modelIds = modelIds;
    }

    public <T> NotFoundException(final Class<T> modelClass, final Set<Long> modelIds) {
        this(modelClass.getSimpleName(), modelIds);
    }
}
