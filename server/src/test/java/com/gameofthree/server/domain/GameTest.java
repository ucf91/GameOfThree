package com.gameofthree.server.domain;

import com.gameofthree.server.domain.exception.GameException;
import com.gameofthree.server.domain.exception.GameRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {

    @DisplayName("when add two player to a game then it's status changes to READY")
    @Test
    public void testGameAddTwoPlayer_ReadyState() throws GameException {
        //given we have a game and two players
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";

        // when add one player
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);
        // then
        assertThat("game status should be not started", game.getGameStatus(), equalTo(GameStatus.NOT_STARTED));

        // and when add second player
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
        //then
        assertThat("game status should be ready", game.getGameStatus(), equalTo(GameStatus.READY));
    }

    @DisplayName("when start game then status should be running")
    @Test
    public void testGameStart_RunningState() throws GameException {
        //given we have a game and two players
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);

        // when add one player
        game.start(new Turn(60));

        // then
        assertThat("game status should be Running", game.getGameStatus(), equalTo(GameStatus.RUNNING));
    }

    @DisplayName("when start game not ready yet then exception should be thrown")
    @Test
    public void testGameStart_notReady() throws GameException {
        //given we have a game and two players
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String nickName1 = "nickName1";
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);

        // when/ then
        assertThrows(GameException.class, () -> game.start(new Turn(60)), "Game exception should be thrown as it's not ready");
    }

    @DisplayName("when 3 concurrent players want to be added then two will be accepted and third one will throw exception")
    @Test
    public void testGameAddThreePlayer_concurrentTest() throws GameException, InterruptedException {
        //given
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String sessionId3 = "sessionId3";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        String nickName3 = "nickName3";
        AtomicBoolean gameExceptionThrown = new AtomicBoolean(false);

        // when add 3 players at same time
        ExecutorService es = Executors.newFixedThreadPool(3);
        es.execute(() -> {
            try {
                game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);
            } catch (GameException e) {
                gameExceptionThrown.set(true);
            }
        });
        es.execute(() -> {
            try {
                game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
            } catch (GameException e) {
                gameExceptionThrown.set(true);
            }
        });
        es.execute(() -> {
            try {
                game.addPlayer(sessionId3, nickName3, PlayMode.AUTO);
            } catch (GameException e) {
                gameExceptionThrown.set(true);
            }
        });

        es.shutdown();
        es.awaitTermination(3, TimeUnit.SECONDS);
        // then
        assertThat("game exception should be thrown", gameExceptionThrown.get(), equalTo(true));

        //then
        assertThat("players size in game should be two", game.getPlayers().size(), equalTo(2));
    }

    @DisplayName("when play turn in game with valid number then number after division by three should be returned")
    @Test
    public void testGamePlay_validNumber() throws GameException, GameRuleException {
        //given
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);
        game.start(new Turn(56));

        // when
        int result = game.play("sessionId2", new Turn(57));

        // then
        assertThat("play turn result is 19", result, equalTo(19));
    }

    @DisplayName("when play turn in game with invalid (Not divisible by 3) number then GameRuleException should be thrown")
    @Test
    public void testGamePlay_invalidNumber() throws GameException {
        //given
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);
        game.start(new Turn(56));

        // when/then
        assertThrows(GameRuleException.class, () -> game.play("sessionId1", new Turn(58)), "Game Rule exception should be thrown as it's not divisible by 3");
    }

    @DisplayName("when play turn in game reaches 1 then game status should be finished and winner player should be set")
    @Test
    public void testGamePlay_finishGame() throws GameException, GameRuleException {
        //given
        Game game = new Game();
        String sessionId1 = "sessionId1";
        String sessionId2 = "sessionId2";
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);
        game.addPlayer(sessionId1, nickName1, PlayMode.AUTO);
        game.start(new Turn(4));

        // when
        int result = game.play("sessionId2", new Turn(3));


        // then
        assertThat("game status should be finished", game.getGameStatus(), equalTo(GameStatus.FINISHED));
        assertThat("winner player should be present", game.getWinnerPlayer().isPresent(), equalTo(true));
    }

    @DisplayName("when stop game then game status should be finished")
    @Test
    public void testStopGame_finishedStatus() throws GameException {
        //given
        Game game = new Game();
        String sessionId2 = "sessionId2";
        String nickName2 = "nickName2";
        game.addPlayer(sessionId2, nickName2, PlayMode.AUTO);

        // when
        game.stopGame();

        // then
        assertThat("game status should be finished", game.getGameStatus(), equalTo(GameStatus.FINISHED));
    }
}
