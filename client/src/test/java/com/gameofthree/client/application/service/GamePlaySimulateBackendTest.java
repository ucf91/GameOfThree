package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.service.impl.DefaultGamePlayService;
import com.gameofthree.client.application.view.AdditionView;
import com.gameofthree.client.application.view.GameIdleView;
import com.gameofthree.client.application.view.MessagesView;
import com.gameofthree.client.handler.TestGameEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.gameofthree.client.application.model.PlayMode.MANUAL;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class GamePlaySimulateBackendTest {

    @MockBean
    private GameIdleView gameIdleView;
    @MockBean
    private MessagesView messagesView;
    @SpyBean
    @Qualifier("testHandler")
    private GameEventsHandler gameEventsHandler;
    @MockBean
    private ConnectionService connectionService;
    @MockBean
    private AdditionView additionView;
    @Autowired
    private NumberGenerationService numberGenerationService;

    private GamePlayService gamePlayService;


    @PostConstruct
    public void initialize() {
        this.gamePlayService = new DefaultGamePlayService(gameEventsHandler, connectionService, messagesView, gameIdleView, additionView, numberGenerationService);
    }

    // begin is considered as a round, every play round would need skip command to know the status of his move
    //so a game consists of 3 rounds will wait next commands 5 times plus final stop command which exits the game
    @DisplayName("when player normally play for 3 rounds then player should wait 5 times for next commands")
    @Test
    public void testBeginPlay_callPlayTurn() throws InterruptedException, ExecutionException, GameException {
        //given that backend would provide the player with 6 commands
        GameCommand beginCommand = generate3RoundsGameCommands();
        // given player input addition would always equal 0
        doReturn(0).when(additionView).render();

        //when
        gamePlayService.beginPlay(beginCommand);

        //then
        verify(gameEventsHandler, times(5)).getNextCommand();
    }

    private GameCommand generate3RoundsGameCommands() {
        GameCommand beginCommand = GameCommand.builder()
                .command(Command.BEGIN)
                .gameId(1l)
                .message("")
                .build();
        GameCommand playCommand = GameCommand.builder()
                .command(Command.PLAY)
                .gameId(1l)
                .message("message")
                .previousNumber(9)
                .resultingNumber(6)
                .playMode(MANUAL)
                .build();
        GameCommand skipCommand = GameCommand.builder()
                .command(Command.SKIP)
                .gameId(1l)
                .message("message")
                .lastMoveValid(true)
                .build();
        GameCommand playCommand2 = GameCommand.builder()
                .command(Command.PLAY)
                .gameId(1l)
                .message("message")
                .previousNumber(6)
                .resultingNumber(3)
                .playMode(MANUAL)
                .build();
        GameCommand skipCommand2 = GameCommand.builder()
                .command(Command.SKIP)
                .gameId(1l)
                .message("message")
                .lastMoveValid(true)
                .build();
        GameCommand stopCommand = GameCommand.builder()
                .command(Command.STOP)
                .gameId(1l)
                .message("stop message")
                .previousNumber(3)
                .resultingNumber(1)
                .playMode(MANUAL)
                .build();

        TestGameEventHandler testHandler = (TestGameEventHandler) gameEventsHandler;
        testHandler.futureCommands.add(CompletableFuture.completedFuture(playCommand));
        testHandler.futureCommands.add(CompletableFuture.completedFuture(skipCommand));
        testHandler.futureCommands.add(CompletableFuture.completedFuture(playCommand2));
        testHandler.futureCommands.add(CompletableFuture.completedFuture(skipCommand2));
        testHandler.futureCommands.add(CompletableFuture.completedFuture(stopCommand));
        return beginCommand;
    }

}
