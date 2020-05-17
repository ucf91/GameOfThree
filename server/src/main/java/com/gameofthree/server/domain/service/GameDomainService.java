package com.gameofthree.server.domain.service;

import com.gameofthree.server.application.dto.GameDetails;
import com.gameofthree.server.application.dto.GamePlayTurnDetails;
import com.gameofthree.server.domain.PlayMode;
import com.gameofthree.server.domain.Turn;
import com.gameofthree.server.domain.exception.GameException;

import java.util.Optional;

public interface GameDomainService {
    GameDetails registerPlayer(String nickName, String sessionIdentity, PlayMode playMode);

    GamePlayTurnDetails playTurn(long gameId, Turn turn, int previousNumber, String sessionId) throws GameException;

    Optional<String> forceStopGame(String playerSessionId);
}
