package com.gameofthree.server.application.controller;

import com.gameofthree.server.application.dto.GameCommand;
import com.gameofthree.server.application.dto.RegistrationRequest;
import com.gameofthree.server.application.dto.RegistrationResponse;
import com.gameofthree.server.application.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class GameController {
    private final GameService gameService;

    // since our websocket solution is based on event driven architecture I preferred to name the endpoints as verbs/actions
    @MessageMapping("/register")
    @SendToUser("/topic/registration")
    public RegistrationResponse registerPlayer(RegistrationRequest registrationRequest, Principal principal) {
        return gameService.registerPlayer(registrationRequest.getNickName(), principal.getName(), registrationRequest.getPlayMode());
    }

    @MessageMapping("/play")
    public void play(GameCommand gameCommand, Principal principal) {
        gameService.playTurn(gameCommand, principal.getName());
    }
}
