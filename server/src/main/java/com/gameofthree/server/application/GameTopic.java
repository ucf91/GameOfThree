package com.gameofthree.server.application;

import lombok.Getter;

@Getter
public enum GameTopic {
    REGISTRATION_TOPIC("/topic/registration"),
    GAMEPLAY_TOPIC("/topic/gameplay");
    private final String topicName;

    GameTopic(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return topicName;
    }
}