package com.gameofthree.client.application.service;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class GameStartupServiceTest {

    @Autowired
    GameStartupService gameStartupService;

    @MockBean
    GamePlayService gamePlayService;
    @MockBean
    RegistrationService registrationService;
    @MockBean
    ConnectionService connectionService;

    @DisplayName("when receive game command begin after registeration then begin play should be called")
    @Test
    public void testStartNewGame_firstPlayer() throws InterruptedException, ExecutionException, GameException {
        //given
        GameCommand gameCommand = GameCommand.builder().command(Command.BEGIN).build();
        doReturn(gameCommand).when(registrationService).registerPlayer();

        //when
        gameStartupService.startNewGame();

        //then
        verify(gamePlayService, times(1)).beginPlay(gameCommand);
    }

    @DisplayName("when receive game command play after registeration then play turn should be called")
    @Test
    public void testStartNewGame_secondPlayer() throws InterruptedException, ExecutionException, GameException {
        //given
        GameCommand gameCommand = GameCommand.builder().command(Command.PLAY).build();
        doReturn(gameCommand).when(registrationService).registerPlayer();

        //when
        gameStartupService.startNewGame();

        //then
        verify(gamePlayService, times(1)).playTurn(gameCommand);
    }

}
