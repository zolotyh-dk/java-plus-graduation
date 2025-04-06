package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.SimilarityMapper;
import ru.practicum.ewm.model.Similarity;
import ru.practicum.ewm.repository.SimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSimilarityService {
    private final SimilarityMapper similarityMapper;
    private final SimilarityRepository similarityRepository;

    public void process(EventSimilarityAvro eventSimilarityAvro) {
        Similarity newSimilarity = similarityMapper.mapToSimilarity(eventSimilarityAvro);
        updateOrCreateSimilarity(newSimilarity);
    }

    private void updateOrCreateSimilarity(Similarity newSimilarity) {
        similarityRepository.findByEventAIdAndEventBId(newSimilarity.getEventAId(), newSimilarity.getEventBId()
        ).ifPresentOrElse(
                existingSimilarity -> updateSimilarity(existingSimilarity, newSimilarity),
                () -> saveNewSimilarity(newSimilarity)
        );
    }

    private void updateSimilarity(Similarity existingSimilarity, Similarity newSimilarity) {
        log.info("Updating similarity: eventAId: {}, eventBId: {}, oldSimilarity: {}, newSimilarity: {}",
                existingSimilarity.getEventAId(),
                existingSimilarity.getEventBId(),
                existingSimilarity.getSimilarity(),
                newSimilarity.getSimilarity());
        existingSimilarity.setSimilarity(newSimilarity.getSimilarity());
        existingSimilarity.setTimestamp(newSimilarity.getTimestamp());
        similarityRepository.save(existingSimilarity);
    }

    private void saveNewSimilarity(Similarity newSimilarity) {
        log.info("Saving new similarity: eventAId: {}, eventBId: {}, similarity: {}",
                newSimilarity.getEventAId(),
                newSimilarity.getEventBId(),
                newSimilarity.getSimilarity());
        similarityRepository.save(newSimilarity);
    }
}
