package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GameDetails {
    private long gameId;
    private GameStatus gameStatus;
    private PlayerDetails opponentPlayer;
}
