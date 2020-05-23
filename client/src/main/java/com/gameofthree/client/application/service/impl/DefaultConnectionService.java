package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.handler.ConnectionHandler;
import com.gameofthree.client.application.service.ConnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class DefaultConnectionService implements ConnectionService {
    private final ConnectionHandler connectionHandler;
    private final WebSocketStompClient webSocketStompClient;

    @Value("${game-server.address}")
    private String gameServerUrl;

    private Optional<StompSession> currentSessionOptional;

    public DefaultConnectionService(ConnectionHandler connectionHandler, WebSocketStompClient webSocketStompClient) {
        this.connectionHandler = connectionHandler;
        this.webSocketStompClient = webSocketStompClient;
        this.currentSessionOptional = Optional.empty();
    }

    public boolean connect() throws ExecutionException, InterruptedException {
        ListenableFuture<StompSession> stompSessionListenableFuture = webSocketStompClient.connect(gameServerUrl, connectionHandler);
        this.currentSessionOptional = Optional.of(stompSessionListenableFuture.get());
        // wait until after connected to avoid TEXT_PARTIAL_WRITING error
        connectionHandler.isAfterConnectedFuture().get();
        return true;
    }

    @Override
    public void send(String url, Object object) throws GameException {
        this.currentSessionOptional.orElseThrow(() -> new GameException("No active session found"))
                .send(url, object);
    }

    @Override
    public void disconnect() {
        this.currentSessionOptional.ifPresent(StompSession::disconnect);
    }
}
