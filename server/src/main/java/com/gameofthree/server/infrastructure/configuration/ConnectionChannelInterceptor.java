package com.gameofthree.server.infrastructure.configuration;

import com.gameofthree.server.application.handler.PlayerDisconnectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConnectionChannelInterceptor implements ChannelInterceptor {
    private final PlayerDisconnectionHandler playerDisconnectionHandler;

    public ConnectionChannelInterceptor(@Lazy PlayerDisconnectionHandler playerDisconnectionHandler) {
        this.playerDisconnectionHandler = playerDisconnectionHandler;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor
                = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info(String.format("%s is connected", accessor.getUser().getName()));
        }
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            playerDisconnectionHandler.cleanResources(accessor.getUser().getName());
            log.info(String.format("%s is disconnected", accessor.getUser().getName()));
        }
        return message;
    }

}
