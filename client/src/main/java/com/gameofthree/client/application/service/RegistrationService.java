package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.model.GameCommand;

import java.util.concurrent.ExecutionException;

public interface RegistrationService {

    GameCommand registerPlayer() throws ExecutionException, InterruptedException, GameException;
}
