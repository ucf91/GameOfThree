package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.model.PlayMode;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.model.RegistrationRequest;
import com.gameofthree.client.application.model.RegistrationResponse;
import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.service.ConnectionService;
import com.gameofthree.client.application.service.GamePlayService;
import com.gameofthree.client.application.service.GameStartupService;
import com.gameofthree.client.application.view.GameIdleView;
import com.gameofthree.client.application.view.RegisterPlayerView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DefaultGameStartupService implements GameStartupService {

    @Value("${game-server.endpoint.register}")
    private String registerEndpointUrl;

    private final RegisterPlayerView registerPlayerView;
    private final GameIdleView gameIdleView;

    private final ConnectionService connectionService;
    private final GameEventsHandler gameEventsHandler;
    private final GamePlayService gamePlayService;

    @Override
    public void startNewGame() throws ExecutionException, InterruptedException, GameException {
        GameCommand gameCommand = registerPlayer();
        if (Command.BEGIN == gameCommand.getCommand()) {
            gamePlayService.beginPlay(gameCommand);
        }
        if (Command.PLAY == gameCommand.getCommand()) {
            gamePlayService.playTurn(gameCommand);
        }
        exitGame();
    }

    private GameCommand registerPlayer() throws ExecutionException, InterruptedException {
        Map<String, String> inputs = getUserInputs();
        PlayMode playMode = ("A".equalsIgnoreCase(inputs.get("playMode"))) ? PlayMode.AUTO : PlayMode.MANUAL;

        sendRegistrationRequest(inputs.get("nickName"), playMode);
        RegistrationResponse registrationResponse = gameEventsHandler.getRegistrationResponse().get();

        // wait for other player to join
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
            exitGame();
            return GameCommand.builder().command(Command.STOP).build();
        }
        return gameCommandFuture.get();
    }

    private Map<String, String> getUserInputs() {
        return (Map<String, String>) registerPlayerView.render();
    }

    private void sendRegistrationRequest(String nickName, PlayMode playMode) throws ExecutionException, InterruptedException {
        StompSession session = connectionService.connect();
        session.send(registerEndpointUrl, new RegistrationRequest(nickName, playMode));
    }

    private void exitGame() {
        connectionService.getCurrentSession().ifPresent(StompSession::disconnect);
    }
}
