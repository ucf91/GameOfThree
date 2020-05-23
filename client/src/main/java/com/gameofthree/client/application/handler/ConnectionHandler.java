package com.gameofthree.client.application.handler;

import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.model.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ConnectionHandler extends StompSessionHandlerAdapter {

    @Value("${topics.registration}")
    private String registrationTopicUrl;
    @Value("${topics.gameplay}")
    private String gameplayTopicUrl;

    @Qualifier("defaultHandler")
    private final GameEventsHandler gameEventsHandler;

    private CompletableFuture<Boolean> afterConnectedFuture;


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(registrationTopicUrl, this);
        session.subscribe(gameplayTopicUrl, this);
        afterConnectedFuture.complete(true);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (registrationTopicUrl.equals(headers.getDestination())) {
            gameEventsHandler.handleRegistrationCallback((RegistrationResponse) payload);
        }
        if (gameplayTopicUrl.equals(headers.getDestination())) {
            gameEventsHandler.handleGamePlayEvents((GameCommand) payload);
        }
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        // abnormal termination if server is not running
        System.out.println("Can't connect to server");
        System.exit(1);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        if (registrationTopicUrl.equals(headers.getDestination())) {
            return RegistrationResponse.class;
        }
        if (gameplayTopicUrl.equals(headers.getDestination())) {
            return GameCommand.class;
        }
        return String.class;
    }

    public CompletableFuture<Boolean> isAfterConnectedFuture() {
        afterConnectedFuture = new CompletableFuture<>();
        return afterConnectedFuture;
    }
}
