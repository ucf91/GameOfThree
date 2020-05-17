package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.PlayMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDetails {
    private String nickName;
    private String sessionId;
    private PlayMode playMode;
}
