package com.gameofthree.client.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    private String nickName;
    private PlayMode playMode;
}
