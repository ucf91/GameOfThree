package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.model.PlayMode;
import com.gameofthree.client.application.view.AdditionView;
import com.gameofthree.client.application.view.GameIdleView;
import com.gameofthree.client.application.view.MessagesView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class GamePlayServiceTest {
    @Autowired
    private GamePlayService gamePlayService;

    @MockBean
    private GameIdleView gameIdleView;
    @MockBean
    private MessagesView messagesView;
    @MockBean
    @Qualifier("defaultHandler")
    private GameEventsHandler gameEventsHandler;
    @MockBean
    private ConnectionService connectionService;
    @MockBean
    private AdditionView additionView;

    @DisplayName("when command is stop then correspondent messages would be prompted to user and won't wait for next command")
    @Test
    public void testPlayTurn_stopCommand() throws InterruptedException, ExecutionException, GameException {
        //given
        GameCommand gameCommand = GameCommand.builder().command(Command.STOP).build();

        //when
        gamePlayService.playTurn(gameCommand);

        //then
        verify(messagesView, times(1)).renderAfterNewLine(gameCommand.getMessage());
        verify(gameIdleView, times(1)).renderPressEnterKey();
        verify(gameEventsHandler, times(0)).getNextCommand();
    }

    @DisplayName("when command is skip then correspondent messages would be prompted to user and wait for next command")
    @Test
    public void testPlayTurn_skipCommand() throws InterruptedException, ExecutionException, GameException {
        //given
        GameCommand gameCommand = GameCommand.builder()
                .command(Command.SKIP)
                .message("message")
                .build();
        CompletableFuture<GameCommand> gameCommandFuture = CompletableFuture.completedFuture(GameCommand.builder().build());
        doReturn(gameCommandFuture).when(gameEventsHandler).getNextCommand();

        //when
        gamePlayService.playTurn(gameCommand);

        //then
        verify(messagesView, times(1)).render(gameCommand.getMessage());
        verify(messagesView, times(1)).render("please wait your turn");
        verify(gameEventsHandler, times(1)).getNextCommand();
    }

}
