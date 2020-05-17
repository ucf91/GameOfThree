package com.gameofthree.server.domain.service;

import com.gameofthree.server.application.dto.GameDetails;
import com.gameofthree.server.application.dto.GamePlayTurnDetails;
import com.gameofthree.server.application.dto.PlayerDetails;
import com.gameofthree.server.domain.*;
import com.gameofthree.server.domain.exception.GameException;
import com.gameofthree.server.domain.exception.GameRuleException;
import com.gameofthree.server.domain.repository.GameRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameDomainServiceImpl implements GameDomainService {
    private final GameRepo gameRepo;

    @Override
    public GameDetails registerPlayer(String nickName, String sessionId, PlayMode playMode) {
        // first find if any open not started games
        // ideally only one game should be open
        List<Game> openGames = gameRepo.findAllByGameStatus(GameStatus.NOT_STARTED);
        Game game;
        if (openGames.isEmpty()) {
            game = gameRepo.save(new Game());
        } else {
            game = openGames.get(0);
        }

        PlayerDetails opponentPlayerDetails = new PlayerDetails();
        try {
            game.addPlayer(sessionId, nickName, playMode);
            if (GameStatus.READY == game.getGameStatus()) {
                Player opponentPlayer = game.getOpponentPlayer(sessionId).orElseThrow(GameException::new);
                opponentPlayerDetails.setNickName(opponentPlayer.getNickName());
                opponentPlayerDetails.setSessionId(opponentPlayer.getSessionId());
            }
        } catch (GameException e) {
            // in case of many parallel threads of more than two users the left users will call registerPlayer
            // method again looking for new game to join
            registerPlayer(nickName, sessionId, playMode);
        }
        // never expose entities outside domain service and of course using sophisticated mappers like dozer would be better
        return new GameDetails(game.getId(), game.getGameStatus(), opponentPlayerDetails);
    }

    @Override
    public GamePlayTurnDetails playTurn(long gameId, Turn turn, int previousNumber, String playerSessionId) throws GameException {
        Game game = getGame(gameId, playerSessionId);
        GamePlayTurnDetails gamePlayTurnDetails = getGameDetails(playerSessionId, game);

        if (game.getGameStatus() == GameStatus.READY) {
            return doStart(turn, game, gamePlayTurnDetails);
        }

        int result = 0;
        try {
            result = game.play(playerSessionId, turn);
            gamePlayTurnDetails = gamePlayTurnDetails.toBuilder()
                    .gameStatus(game.getGameStatus())
                    .build();
        } catch (GameRuleException ex) {
            return gamePlayTurnDetails.toBuilder().gameStatus(game.getGameStatus()).turnResult(previousNumber).validMove(false).build();
        }

        return buildGamePlayTurnDetails(game, gamePlayTurnDetails, result);
    }

    @Override
    public Optional<String> forceStopGame(String playerSessionId) {
        Optional<Game> gameOpt = gameRepo.findBySessionId(playerSessionId);
        final String[] otherPlayerSessionId = {null};
        gameOpt.ifPresent(game -> {
            game.stopGame();
            game.getOpponentPlayer(playerSessionId).ifPresent(player -> otherPlayerSessionId[0] = player.getSessionId());
        });
        return Optional.ofNullable(otherPlayerSessionId[0]);
    }


    private Game getGame(long gameId, String playerSessionId) throws GameException {
        return gameRepo.findByIdAndPlayerSessionId(gameId, playerSessionId)
                .orElseThrow(() -> new GameException("Game not found"));
    }

    private static GamePlayTurnDetails getGameDetails(String playerSessionId, Game game) throws GameException {
        Player player = game.getPlayer(playerSessionId).orElseThrow(GameException::new);
        Player opponentPlayer = game.getOpponentPlayer(playerSessionId).orElseThrow(GameException::new);
        return GamePlayTurnDetails.builder()
                .playerDetails(new PlayerDetails(player.getNickName(), player.getSessionId(), player.getPlayMode()))
                .opponentPlayerDetails(new PlayerDetails(opponentPlayer.getNickName(), opponentPlayer.getSessionId(), opponentPlayer.getPlayMode()))
                .build();
    }

    public Optional<Player> getOpponentPlayer(long gameId, String otherPlayerSessionId) throws GameException {
        Game game = getGame(gameId, otherPlayerSessionId);
        return game.getOpponentPlayer(otherPlayerSessionId);
    }


    private static GamePlayTurnDetails doStart(Turn turn, Game game, GamePlayTurnDetails gamePlayTurnDetails) throws GameException {
        game.start(turn);
        return gamePlayTurnDetails.toBuilder()
                .gameStatus(game.getGameStatus())
                .turnResult(turn.getNumber())
                .validMove(true)
                .build();
    }

    private GamePlayTurnDetails buildGamePlayTurnDetails(Game game, GamePlayTurnDetails gamePlayTurnDetails, int result) throws GameException {
        gamePlayTurnDetails = gamePlayTurnDetails.toBuilder()
                .turnResult(result)
                .validMove(true)
                .build();

        if (result == 1) {
            Player winnerPlayer = game.getWinnerPlayer().orElseThrow(() -> new GameException("winner player not found"));
            gamePlayTurnDetails = gamePlayTurnDetails.toBuilder()
                    .winnerPlayer(Optional.of(new PlayerDetails(winnerPlayer.getNickName(), winnerPlayer.getSessionId(), winnerPlayer.getPlayMode())))
                    .build();
        }
        return gamePlayTurnDetails;
    }
}
