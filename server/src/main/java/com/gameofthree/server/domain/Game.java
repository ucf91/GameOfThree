package com.gameofthree.server.domain;

import com.gameofthree.server.domain.exception.GameException;
import com.gameofthree.server.domain.exception.GameRuleException;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Game extends RootEntity {
    @Getter
    private GameStatus gameStatus;
    private int currentNumber = 0;
    private Map<String, Player> players;
    private Player winnerPlayer;
    private static AtomicLong indexCounter = new AtomicLong(0);
    private final Object lock = new Object();

    public Game() {
        this.gameStatus = GameStatus.NOT_STARTED;
        this.players = new LinkedHashMap<>(2);
    }

    public void addPlayer(String sessionId, String nickName, PlayMode playMode) throws GameException {
        Player player = new Player(sessionId, nickName, playMode);
        // in case we had more than two multithreads of players trying to register to the same room
        synchronized (lock) {
            if (players.size() == 2) {
                throw new GameException("Game is full of players");
            }
            player.setId(indexCounter.incrementAndGet());
            if (players.containsKey(sessionId)) {
                throw new GameException("Player already added to the game");
            }
            players.put(player.getSessionId(), player);
            gameStatus = (players.size() == 2) ? GameStatus.READY : gameStatus;
        }
    }

    public void start(Turn turn) throws GameException {
        if (!isOkToStart()) {
            throw new GameException("Game can't start");
        }
        gameStatus = GameStatus.RUNNING;
        currentNumber = turn.getNumber();
    }

    public Integer play(String playerSessionId, Turn turn) throws GameRuleException, GameException {
        if (GameStatus.RUNNING != gameStatus) {
            throw new GameException("Game should be in running state");
        }
        if (currentNumber != 0) {
            validateGameRule(turn);
        }
        Player player = getPlayer(playerSessionId).orElseThrow(GameException::new);
        currentNumber = player.move(turn);
        if (currentNumber == 1) {
            gameStatus = GameStatus.FINISHED;
            winnerPlayer = player;
        }
        return currentNumber;
    }

    public void stopGame() {
        gameStatus = GameStatus.FINISHED;
    }

    public Map<String, Player> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public Optional<Player> getPlayer(String playerSessionId) {
        return Optional.ofNullable(players.get(playerSessionId));
    }

    public Optional<Player> getOpponentPlayer(String playerSessionId) {
        return players.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(playerSessionId))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Optional<Player> getWinnerPlayer() {
        return Optional.ofNullable(winnerPlayer);
    }


    private boolean isOkToStart() {
        if (players.size() == 2 && GameStatus.READY == this.gameStatus) {
            return true;
        }
        return false;
    }

    private void validateGameRule(Turn turn) throws GameRuleException {
        int newNumber = turn.getNumber();
        // addition should be within [-1,0,1] and the new number should be divisible by 3
        if (newNumber - currentNumber > 1 || newNumber - currentNumber < -1 || newNumber % 3 != 0) {
            throw new GameRuleException("not a valid move, please try again");
        }
    }

}
