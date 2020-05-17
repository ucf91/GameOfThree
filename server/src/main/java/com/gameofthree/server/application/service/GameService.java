package com.gameofthree.server.application.service;

import com.gameofthree.server.application.dto.GameCommand;
import com.gameofthree.server.application.dto.RegistrationResponse;
import com.gameofthree.server.domain.PlayMode;

public interface GameService {
    RegistrationResponse registerPlayer(String nickName, String userSessionId, PlayMode playMode);
    void playTurn(GameCommand gameCommand, String userSessionId);
    void terminateGame(String playerSessionId);
}
