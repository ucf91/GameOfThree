package com.gameofthree.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Player extends Entity {

    private String sessionId;
    private String nickName;
    private PlayMode playMode;

    int move(Turn turn) {
        return turn.getNumber() / 3;
    }
}
