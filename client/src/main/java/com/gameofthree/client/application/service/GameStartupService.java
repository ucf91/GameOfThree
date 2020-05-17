package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;

import java.util.concurrent.ExecutionException;

public interface GameStartupService {
    void startNewGame() throws ExecutionException, InterruptedException, GameException;
}
