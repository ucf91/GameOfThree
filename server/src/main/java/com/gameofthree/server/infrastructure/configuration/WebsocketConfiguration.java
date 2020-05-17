package com.gameofthree.server.infrastructure.configuration;

import com.gameofthree.server.application.handler.DetermineUserHandshakeHandler;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Component
@AllArgsConstructor
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final ConnectionChannelInterceptor connectionChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/game");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gameofthree")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DetermineUserHandshakeHandler())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(connectionChannelInterceptor);
    }
}
