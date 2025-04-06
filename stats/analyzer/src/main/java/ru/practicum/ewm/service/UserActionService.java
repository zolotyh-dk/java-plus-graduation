package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.WeightMapper;
import ru.practicum.ewm.model.Weight;
import ru.practicum.ewm.repository.WeightRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActionService {
    private final WeightRepository weightRepository;
    private final WeightMapper weightMapper;

    public void process(UserActionAvro userActionAvro) {
        Weight newWeight = weightMapper.toWeight(userActionAvro);
        updateOrCreateWeight(newWeight);
    }

    private void updateOrCreateWeight(Weight newWeight) {
        weightRepository.findByEventIdAndUserId(newWeight.getEventId(), newWeight.getUserId())
                .ifPresentOrElse(
                        existingWeight -> updateIfGreater(existingWeight, newWeight),
                        () -> saveNewWeight(newWeight)
                );
    }

    private void updateIfGreater(Weight existingWeight, Weight newWeight) {
        if (newWeight.getWeight() > existingWeight.getWeight()) {
            log.info("Updating weight for userId: {}, eventId: {} from {} to {}",
                    existingWeight.getUserId(),
                    existingWeight.getEventId(),
                    existingWeight.getWeight(),
                    newWeight.getWeight());
            existingWeight.setWeight(newWeight.getWeight());
            existingWeight.setTimestamp(newWeight.getTimestamp());
            weightRepository.save(existingWeight);
        } else {
            log.info("Existing weight: {} for userId: {}, eventId: {} is greater or equal to new weight: {}, skipping update",
                    existingWeight.getWeight(),
                    existingWeight.getUserId(),
                    existingWeight.getEventId(),
                    newWeight.getWeight());
        }
    }

    private void saveNewWeight(Weight newWeight) {
        log.info("Saving new weight for userId={}, eventId={}, weight={}",
                newWeight.getUserId(),
                newWeight.getEventId(),
                newWeight.getWeight());
        weightRepository.save(newWeight);
    }
}
