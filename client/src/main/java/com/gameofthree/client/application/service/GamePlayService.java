package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.model.GameCommand;

import java.util.concurrent.ExecutionException;

public interface GamePlayService {
    void beginPlay(GameCommand gameCommand) throws ExecutionException, InterruptedException, GameException;

    void playTurn(GameCommand gameCommand) throws ExecutionException, InterruptedException, GameException;
}
