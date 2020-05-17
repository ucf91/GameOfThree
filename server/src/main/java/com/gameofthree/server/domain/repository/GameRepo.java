package com.gameofthree.server.domain.repository;

import com.gameofthree.server.domain.Game;
import com.gameofthree.server.domain.GameStatus;

import java.util.List;
import java.util.Optional;

public interface GameRepo {
    Game save(Game game);

    Optional<Game> findByIdAndPlayerSessionId(long id, String sessionId);

    List<Game> findAllByGameStatus(GameStatus gameStatus);

    Optional<Game> findBySessionId(String playerSessionId);
}
