package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class RegistrationResponse {
    long gameId;
    GameStatus gameStatus;
    String opponentPlayerName;
}
