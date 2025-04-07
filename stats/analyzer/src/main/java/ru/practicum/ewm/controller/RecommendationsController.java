package ru.practicum.ewm.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.service.UserActionService;
import ru.practicum.ewm.stats.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.message.RecommendedEventProto;
import ru.practicum.ewm.stats.service.RecommendationsControllerGrpc;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final UserActionService userActionService;

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Received interactions count request for eventIds: {}", request.getEventIdList());
            List<RecommendedEventProto> events = userActionService.getTotalInteractionWeight(request.getEventIdList());
            events.forEach(event -> {
                responseObserver.onNext(event);
                log.info("Response to interactions count request: eventId: {}, interactions: {}",
                        event.getEventId(), event.getScore());
            });
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }
}