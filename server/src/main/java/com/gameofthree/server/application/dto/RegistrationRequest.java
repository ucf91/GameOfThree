package com.gameofthree.server.application.dto;

import com.gameofthree.server.domain.PlayMode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    private String nickName;
    private PlayMode playMode;
}
