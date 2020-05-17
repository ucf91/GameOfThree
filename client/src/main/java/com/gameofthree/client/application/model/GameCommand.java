package com.gameofthree.client.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class GameCommand {
    private long gameId;
    private Command command;
    private Integer resultingNumber;
    private Integer previousNumber;
    private String message;
    private boolean lastMoveValid;
    private PlayMode playMode;
}
