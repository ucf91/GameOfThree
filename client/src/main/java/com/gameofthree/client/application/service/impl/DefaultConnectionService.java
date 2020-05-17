package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.handler.ConnectionHandler;
import com.gameofthree.client.application.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DefaultConnectionService implements ConnectionService {
    private final ConnectionHandler connectionHandler;
    private final WebSocketStompClient webSocketStompClient;

    @Value("${game-server.address}")
    private String gameServerUrl;

    private StompSession currentSession;

    public StompSession connect() throws ExecutionException, InterruptedException {
        ListenableFuture<StompSession> stompSessionListenableFuture = webSocketStompClient.connect(gameServerUrl, connectionHandler);
        this.currentSession = stompSessionListenableFuture.get();
        // wait until after connected to avoid TEXT_PARTIAL_WRITING error
        connectionHandler.isAfterConnectedFuture().get();
        return this.currentSession;
    }

    public Optional<StompSession> getCurrentSession() {
        return Optional.ofNullable(currentSession);
    }
}
