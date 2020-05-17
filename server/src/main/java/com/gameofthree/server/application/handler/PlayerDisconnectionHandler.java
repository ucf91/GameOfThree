package com.gameofthree.server.application.handler;

import com.gameofthree.server.application.service.GameServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerDisconnectionHandler {
    private final GameServiceImpl gameService;

    public void cleanResources(String playerSessionId) {
        gameService.terminateGame(playerSessionId);
    }
}
