package com.gameofthree.client.application.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class RegistrationResponse {
    long gameId;
    GameStatus gameStatus;
    String opponentPlayerName;
}
