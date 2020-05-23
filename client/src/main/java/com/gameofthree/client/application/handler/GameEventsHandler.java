package com.gameofthree.client.application.handler;

import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.model.RegistrationResponse;
import com.gameofthree.client.application.view.MessagesView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component("defaultHandler")
@RequiredArgsConstructor
public class GameEventsHandler {
    private CompletableFuture<GameCommand> gameCommandFuture;
    private CompletableFuture<RegistrationResponse> registrationResponseFuture;
    private final MessagesView messagesView;

    public void handleGamePlayEvents(GameCommand gameCommand) {
        if (Command.BEGIN == gameCommand.getCommand()) {
            messagesView.renderAfterNewLine("Game is ready press any key to proceed");
        }
        if (Command.PLAY == gameCommand.getCommand()) {
            if (gameCommand.getPreviousNumber() == 0) {
                messagesView.renderAfterNewLine("Game Started ! press any key to proceed");
            } else {
                messagesView.renderAfterNewLine("Now your turn !");
            }
        }
        gameCommandFuture.complete(gameCommand);
    }

    public void handleRegistrationCallback(RegistrationResponse registrationResponse) {
        registrationResponseFuture.complete(registrationResponse);
    }

    public CompletableFuture<RegistrationResponse> getRegistrationResponse() {
        registrationResponseFuture = new CompletableFuture<>();
        return registrationResponseFuture;
    }

    public CompletableFuture<GameCommand> getNextCommand() {
        gameCommandFuture = new CompletableFuture<>();
        return gameCommandFuture;
    }


}
