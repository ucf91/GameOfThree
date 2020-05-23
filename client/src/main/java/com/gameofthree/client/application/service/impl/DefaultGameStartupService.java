package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.service.ConnectionService;
import com.gameofthree.client.application.service.GamePlayService;
import com.gameofthree.client.application.service.GameStartupService;
import com.gameofthree.client.application.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DefaultGameStartupService implements GameStartupService {

    private final GamePlayService gamePlayService;
    private final RegistrationService registrationService;
    private final ConnectionService connectionService;

    @Override
    public void startNewGame() throws ExecutionException, InterruptedException, GameException {
        connectionService.connect();

        GameCommand gameCommand = registrationService.registerPlayer();
        if (Command.BEGIN == gameCommand.getCommand()) {
            gamePlayService.beginPlay(gameCommand);
        }
        if (Command.PLAY == gameCommand.getCommand()) {
            gamePlayService.playTurn(gameCommand);
        }

        connectionService.disconnect();
    }

}
