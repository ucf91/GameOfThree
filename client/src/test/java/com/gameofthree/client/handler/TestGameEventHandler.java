package com.gameofthree.client.handler;

import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.view.MessagesView;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@Component("testHandler")
public class TestGameEventHandler extends GameEventsHandler {

    public Queue<CompletableFuture<GameCommand>> futureCommands = new LinkedList<>();

    public TestGameEventHandler(MessagesView messagesView) {
        super(messagesView);
    }

    @Override
    public CompletableFuture<GameCommand> getNextCommand() {
        return futureCommands.poll();
    }
}
