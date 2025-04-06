package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.model.Similarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Mapper(componentModel = "spring")
public interface SimilarityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventAId", source = "eventA")
    @Mapping(target = "eventBId", source = "eventB")
    Similarity mapToSimilarity(EventSimilarityAvro similarity);
}