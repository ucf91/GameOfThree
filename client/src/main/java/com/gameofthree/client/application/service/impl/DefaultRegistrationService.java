package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.model.*;
import com.gameofthree.client.application.service.ConnectionService;
import com.gameofthree.client.application.service.RegistrationService;
import com.gameofthree.client.application.view.GameIdleView;
import com.gameofthree.client.application.view.RegisterPlayerView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DefaultRegistrationService implements RegistrationService {
    private final RegisterPlayerView registerPlayerView;
    private final GameIdleView gameIdleView;

    private final ConnectionService connectionService;

    @Qualifier("defaultHandler")
    private final GameEventsHandler gameEventsHandler;

    @Value("${game-server.endpoint.register}")
    private String registerEndpointUrl;


    @Override
    public GameCommand registerPlayer() throws ExecutionException, InterruptedException, GameException {
        Map<String, String> inputs = getUserInputs();
        PlayMode playMode = ("A".equalsIgnoreCase(inputs.get("playMode"))) ? PlayMode.AUTO : PlayMode.MANUAL;

        sendRegistrationRequest(inputs.get("nickName"), playMode);
        RegistrationResponse registrationResponse = gameEventsHandler.getRegistrationResponse().get();

        // wait for other player to join
        return waitOtherPlayerJoin(registrationResponse);
    }

    private GameCommand waitOtherPlayerJoin(RegistrationResponse registrationResponse) throws InterruptedException, ExecutionException {
        CompletableFuture<GameCommand> gameCommandFuture = gameEventsHandler.getNextCommand();
        String userInput;
        do {
            String opponentName = registrationResponse.getOpponentPlayerName();
            if (Objects.isNull(opponentName) || opponentName.isEmpty()) {
                userInput = (String) gameIdleView.render();
            } else {
                userInput = (String) gameIdleView.render(opponentName);
            }
        } while (!userInput.equals("exit") && !gameCommandFuture.isDone());
        if ("exit".equals(userInput)) {
            return GameCommand.builder().command(Command.STOP).build();
        }
        return gameCommandFuture.get();
    }

    private Map<String, String> getUserInputs() {
        return (Map<String, String>) registerPlayerView.render();
    }

    private void sendRegistrationRequest(String nickName, PlayMode playMode) throws GameException {
        connectionService.send(registerEndpointUrl, new RegistrationRequest(nickName, playMode));
    }
}
