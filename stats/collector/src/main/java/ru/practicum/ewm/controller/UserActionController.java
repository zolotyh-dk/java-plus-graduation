package ru.practicum.ewm.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.message.UserActionProto;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;

@Slf4j
@GrpcService
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final UserActionService service;
    private final UserActionMapper mapper;

    @Override
    public void collectUserAction(final UserActionProto userActionProto, final StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received user action: userId = {}, eventId = {}, actionType = {}",
                    userActionProto.getUserId(),
                    userActionProto.getEventId(),
                    userActionProto.getActionType());
            log.debug("User action = {}", userActionProto);

            UserActionAvro userActionAvro = mapper.mapToAvro(userActionProto);
            service.send(userActionAvro);
            log.info("Processed user action: userId = {}, eventId = {}, actionType = {}", userActionProto.getUserId(),
                    userActionProto.getEventId(), userActionProto.getActionType());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("User action processing error: {}\nUser action = {}", e.getMessage(), userActionProto, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
