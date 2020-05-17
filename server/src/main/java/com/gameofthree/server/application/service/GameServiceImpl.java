package com.gameofthree.server.application.service;

import com.gameofthree.server.application.dto.*;
import com.gameofthree.server.domain.GameStatus;
import com.gameofthree.server.domain.PlayMode;
import com.gameofthree.server.domain.Turn;
import com.gameofthree.server.domain.exception.GameException;
import com.gameofthree.server.domain.service.GameDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gameofthree.server.application.GameTopic.GAMEPLAY_TOPIC;
import static com.gameofthree.server.domain.GameStatus.FINISHED;

@Service
@AllArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {
    private final GameDomainService gameDomainService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public RegistrationResponse registerPlayer(String nickName, String userSessionId, PlayMode playMode) {
        GameDetails gameDetails = gameDomainService.registerPlayer(nickName, userSessionId, playMode);
        if (GameStatus.READY.equals(gameDetails.getGameStatus())) {
            log.info("game is ready to start");
            GameCommand gameCommand = GameCommand.builder().command(Command.BEGIN)
                    .gameId(gameDetails.getGameId())
                    .build();

            simpMessagingTemplate.convertAndSendToUser(gameDetails.getOpponentPlayer().getSessionId(), GAMEPLAY_TOPIC.toString(), gameCommand);
        }
        // if I needed to reuse the game service in different integration I would make the mapping in the controller but for now it's ok
        return new RegistrationResponse(gameDetails.getGameId(), gameDetails.getGameStatus(), gameDetails.getOpponentPlayer().getNickName());
    }

    @Override
    public void playTurn(GameCommand gameCommand, String userSessionId) {
        GamePlayTurnDetails resultedGamePlay = null;
        try {
            resultedGamePlay = gameDomainService.playTurn(gameCommand.getGameId(), new Turn(gameCommand.getResultingNumber()), gameCommand.getPreviousNumber(), userSessionId);

            if (FINISHED == resultedGamePlay.getGameStatus() && 1 == resultedGamePlay.getTurnResult()) {
                log.info("game {} finished , winner is {}", gameCommand.getGameId(), resultedGamePlay.getWinnerPlayer().get().getNickName());
                finishGame(gameCommand, userSessionId, resultedGamePlay);
                return;
            }

            acknowledgePlayerHisMoveResult(gameCommand, userSessionId, resultedGamePlay);

            askOtherPlayerToPlayHisMove(gameCommand, resultedGamePlay);
        } catch (GameException e) {
            handleGameException(gameCommand, userSessionId, resultedGamePlay, e);
        }
    }

    @Override
    public void terminateGame(String playerSessionId) {
        Optional<String> otherPlayerIdOpt = gameDomainService.forceStopGame(playerSessionId);
        otherPlayerIdOpt.ifPresent(sessionId -> {
            GameCommand gameCommand = GameCommand.builder()
                    .command(Command.STOP)
                    .message("Your opponent is disconnected, Please start new game")
                    .build();
            simpMessagingTemplate.convertAndSendToUser(sessionId, GAMEPLAY_TOPIC.toString(), gameCommand);
        });
    }

    private void handleGameException(GameCommand gameCommand, String userSessionId, GamePlayTurnDetails resultedGamePlay, GameException e) {
        GameCommand returnGameCommand = GameCommand.builder()
                .gameId(gameCommand.getGameId())
                .command(Command.STOP)
                .message(e.getMessage() + " \n please start new game \n\n\n")
                .build();
        List<String> playersSessions = new ArrayList<>();
        playersSessions.add(userSessionId);
        if (Objects.nonNull(resultedGamePlay) && Objects.nonNull(resultedGamePlay.getOpponentPlayerDetails())) {
            playersSessions.add(resultedGamePlay.getOpponentPlayerDetails().getSessionId());
        }
        playersSessions.forEach(sessionId -> simpMessagingTemplate.convertAndSendToUser(sessionId, GAMEPLAY_TOPIC.toString(), returnGameCommand));
    }

    private void askOtherPlayerToPlayHisMove(GameCommand gameCommand, GamePlayTurnDetails resultedGamePlay) {
        simpMessagingTemplate.convertAndSendToUser(resultedGamePlay.getOpponentPlayerDetails().getSessionId(), GAMEPLAY_TOPIC.toString(),
                GameCommand.builder()
                        .command(Command.PLAY)
                        .gameId(gameCommand.getGameId())
                        .previousNumber((gameCommand.getPreviousNumber() == 0) ? gameCommand.getPreviousNumber() : gameCommand.getResultingNumber())
                        .resultingNumber(resultedGamePlay.getTurnResult())
                        .lastMoveValid(resultedGamePlay.isValidMove())
                        .playMode(resultedGamePlay.getOpponentPlayerDetails().getPlayMode())
                        .build());
    }

    private void acknowledgePlayerHisMoveResult(GameCommand gameCommand, String userSessionId, GamePlayTurnDetails resultedGamePlay) {
        if (gameCommand.getPreviousNumber() != 0) {
            simpMessagingTemplate.convertAndSendToUser(userSessionId, GAMEPLAY_TOPIC.toString(),
                    GameCommand.builder().command(Command.SKIP)
                            .gameId(gameCommand.getGameId())
                            .previousNumber(gameCommand.getPreviousNumber())
                            .resultingNumber(resultedGamePlay.getTurnResult())
                            .message((resultedGamePlay.isValidMove()) ? "Nice Move !, Your added number after division is " + resultedGamePlay.getTurnResult()
                                    : "Not a valid move, Your added number is not divisible by 3 !")
                            .build());
        }
    }

    private void finishGame(GameCommand gameCommand, String userSessionId, GamePlayTurnDetails resultedGamePlay) {
        GameCommand returnGameCommand = GameCommand.builder()
                .gameId(gameCommand.getGameId())
                .command(Command.STOP)
                .previousNumber(gameCommand.getResultingNumber())
                .resultingNumber(resultedGamePlay.getTurnResult())
                .build();
        // inform loser opponent
        simpMessagingTemplate.convertAndSendToUser(resultedGamePlay.getOpponentPlayerDetails().getSessionId(), GAMEPLAY_TOPIC.toString(),
                returnGameCommand.toBuilder().message(String.format("Your opponent reached 1, You LOSE ! , Hard Luck %s", resultedGamePlay.getOpponentPlayerDetails().getNickName()))
                        .build());
        // inform winner player
        simpMessagingTemplate.convertAndSendToUser(userSessionId, GAMEPLAY_TOPIC.toString(),
                returnGameCommand.toBuilder()
                        .message(String.format("You reached 1 !!, Congratulations %s, You WIN !", resultedGamePlay.getWinnerPlayer().get().getNickName()))
                        .build());
    }
}
