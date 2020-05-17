package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Builder(toBuilder = true)
@Data
public class GamePlayTurnDetails {
    private String message;
    private GameStatus gameStatus;
    private int turnResult;
    private boolean validMove;
    private PlayerDetails playerDetails;
    private PlayerDetails opponentPlayerDetails;
    private Optional<PlayerDetails> winnerPlayer;
}
