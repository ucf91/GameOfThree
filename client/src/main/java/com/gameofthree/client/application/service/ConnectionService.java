package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import java.util.concurrent.ExecutionException;

public interface ConnectionService {
    boolean connect() throws ExecutionException, InterruptedException;

    void send(String url, Object object) throws GameException;

    void disconnect();

}
