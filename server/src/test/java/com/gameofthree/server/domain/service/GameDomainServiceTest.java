package com.gameofthree.server.domain.service;

import com.gameofthree.server.application.dto.GameDetails;
import com.gameofthree.server.domain.Game;
import com.gameofthree.server.domain.GameStatus;
import com.gameofthree.server.domain.PlayMode;
import com.gameofthree.server.domain.repository.GameRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GameDomainServiceTest {

    @Autowired
    private GameDomainService gameDomainService;

    @MockBean
    private GameRepo gameRepo;

    @DisplayName("when register player first time then gameDetails returned should contains game Id and game status with not stated")
    @Test
    public void testRegisterPlayer_firstPlayer() {
        //given
        Game game = new Game();
        game.setId(1L);
        doReturn(Collections.emptyList()).when(gameRepo).findAllByGameStatus(GameStatus.NOT_STARTED);
        doReturn(game).when(gameRepo).save(ArgumentMatchers.any(Game.class));

        //when
        GameDetails gameDetails = gameDomainService.registerPlayer("nickName", "sessionIdentity", PlayMode.AUTO);

        //then
        assertThat("Game details is not null", gameDetails != null, equalTo(true));
        assertThat("Game id should be equal 1", gameDetails.getGameId(), equalTo(1L));
        assertThat("Game status should be Not started", gameDetails.getGameStatus(), equalTo(GameStatus.NOT_STARTED));
    }

    @DisplayName("when register second player then gameDetails returned should contains game Id and game status with READY")
    @Test
    public void testRegisterPlayer_readyGame() {
        //given
        Game game = new Game();
        game.setId(1L);
        doReturn(Collections.singletonList(game)).when(gameRepo).findAllByGameStatus(GameStatus.NOT_STARTED);
        gameDomainService.registerPlayer("nickName1", "sessionIdentity1", PlayMode.AUTO);

        //when
        GameDetails gameDetails = gameDomainService.registerPlayer("nickName2", "sessionIdentity2", PlayMode.AUTO);

        //then
        assertThat("Game details is not null", gameDetails != null, equalTo(true));
        assertThat("Game id should be equal 1", gameDetails.getGameId(), equalTo(1L));
        assertThat("Game status should be READY", gameDetails.getGameStatus(), equalTo(GameStatus.READY));
    }
    // more & more tests of full coverage are essential of course but I'm out of time
}
