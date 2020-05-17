package com.gameofthree.server.infrastructure.repository.inmemory;

import com.gameofthree.server.domain.Game;
import com.gameofthree.server.domain.GameStatus;
import com.gameofthree.server.domain.repository.GameRepo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryGameRepo implements GameRepo {
    private static final Map<Long, Game> games = new HashMap<>();
    private static AtomicLong indexCounter = new AtomicLong(0);

    @Override
    public Game save(Game game) {
        game.setId(indexCounter.incrementAndGet());
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> findByIdAndPlayerSessionId(long id, String sessionId) {
        return games.entrySet().stream().filter(entry -> entry.getKey() == id)
                .filter(entry -> entry.getValue().getPlayers().containsKey(sessionId))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public List<Game> findAllByGameStatus(GameStatus gameStatus) {
        return games.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(game -> game.getGameStatus() == gameStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Game> findBySessionId(String playerSessionId) {
        return games.entrySet().stream()
                .filter(entry->entry.getValue().getPlayers().containsKey(playerSessionId))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
