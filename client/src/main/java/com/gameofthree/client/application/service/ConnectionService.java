package com.gameofthree.client.application.service;

import org.springframework.messaging.simp.stomp.StompSession;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface ConnectionService {
    StompSession connect() throws ExecutionException, InterruptedException;

    Optional<StompSession> getCurrentSession();

}
