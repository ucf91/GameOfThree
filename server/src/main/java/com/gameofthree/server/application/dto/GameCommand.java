package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.PlayMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class GameCommand {
    private long gameId;
    private Command command;
    private Integer resultingNumber;
    private Integer previousNumber;
    private String message;
    private boolean lastMoveValid;
    private PlayMode playMode;
}
