package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.handler.GameEventsHandler;
import com.gameofthree.client.application.model.Command;
import com.gameofthree.client.application.model.GameCommand;
import com.gameofthree.client.application.model.PlayMode;
import com.gameofthree.client.application.service.ConnectionService;
import com.gameofthree.client.application.service.GamePlayService;
import com.gameofthree.client.application.util.console.ConsoleUtils;
import com.gameofthree.client.application.view.GameIdleView;
import com.gameofthree.client.application.view.MessagesView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DefaultGamePlayService implements GamePlayService {
    private static final String WAIT_TURN_MSG = "please wait your turn";
    @Value("${game-server.endpoint.play}")
    private String playEndpointUrl;

    private final GameEventsHandler gameEventsHandler;
    private final ConnectionService connectionService;
    private final MessagesView messagesView;
    private final GameIdleView gameIdleView;

    @Override
    public void beginPlay(GameCommand gameCommand) throws ExecutionException, InterruptedException, GameException {
        sendRandomWholeNumber(gameCommand);
        messagesView.render(WAIT_TURN_MSG);
        // block thread until receive next game command from the other player
        GameCommand receivedCommand = gameEventsHandler.getNextCommand().get();
        playTurn(receivedCommand);
    }

    @Override
    public void playTurn(GameCommand gameCommand) throws ExecutionException, InterruptedException, GameException {
        //base condition
        if (gameCommand.getCommand() == Command.STOP) {
            // stop command for win / lose / connection interruption or other player surrender
            handleStopCommand(gameCommand);
            return;
        }
        if (gameCommand.getCommand() == Command.SKIP) {
            // always receive skip command after every sent result to show whether it's valid or not
            handleSkipCommand(gameCommand);
        }
        if (gameCommand.getCommand() == Command.PLAY) {
            // every new round starts with report about opponent's move
            showOpponentMoveStatus(gameCommand);

            // choose new addition to number Auto/Manual based on registration preference
            int resultAddedNumber = chooseAnAddition(gameCommand);

            // send play command determining the resulting new number and previous number from previous step
            sendResultingNumberToOpponent(gameCommand, resultAddedNumber);

            //wait receive next command recursively from gameplay topic in other thread using completableFuture
            playTurn(gameEventsHandler.getNextCommand().get());
        }
    }

    private void sendResultingNumberToOpponent(GameCommand gameCommand, int resultAddedNumber) throws GameException {
        connectionService.getCurrentSession()
                .orElseThrow(() -> new GameException("No active session found"))
                .send(playEndpointUrl,
                        GameCommand.builder()
                                .command(Command.PLAY)
                                .gameId(gameCommand.getGameId())
                                .previousNumber(gameCommand.getResultingNumber())
                                .resultingNumber(resultAddedNumber)
                                .build());
    }

    private int chooseAnAddition(GameCommand gameCommand) {
        int userInput = 0;
        if (PlayMode.AUTO == gameCommand.getPlayMode()) {
            // generate random number from -1 to 1
            userInput = new Random().nextInt(3) - 1;
            messagesView.render("Your auto pilot player choosed an addition move of " + userInput);
        } else {
            do {
                messagesView.render("Please choose your addition move from these values [-1, 0, 1]");
                userInput = ConsoleUtils.readNumericInput();
            } while (!List.of(-1, 0, 1).contains(userInput));
        }
        int addedNumber = gameCommand.getResultingNumber() + userInput;
        messagesView.render("Your added number is " + addedNumber);
        return addedNumber;
    }

    private void showOpponentMoveStatus(GameCommand gameCommand) {
        if (gameCommand.getPreviousNumber() == 0) {
            messagesView.render("Your opponent incepted with " + gameCommand.getResultingNumber());
        } else {
            if (gameCommand.isLastMoveValid()) {
                messagesView.render("Your opponent number reached " + gameCommand.getResultingNumber()
                        + " from his added number " + gameCommand.getPreviousNumber());
            } else {
                messagesView.render("Your opponent made wrong move, Current number is still " + gameCommand.getResultingNumber());
            }
        }
    }

    private void handleSkipCommand(GameCommand gameCommand) throws ExecutionException, InterruptedException, GameException {
        messagesView.render(gameCommand.getMessage());
        messagesView.render(WAIT_TURN_MSG);
        playTurn(gameEventsHandler.getNextCommand().get());
    }

    private void handleStopCommand(GameCommand gameCommand) {
        messagesView.renderAfterNewLine(gameCommand.getMessage());
        gameIdleView.renderPressEnterKey();
    }

    private void sendRandomWholeNumber(GameCommand gameCommand) throws GameException {
        int randomWholeNumber = new Random().nextInt(100) + 50;// minimum starting number is 50

        messagesView.render("Your random starting number is : " + randomWholeNumber);

        GameCommand newCommand = GameCommand.builder().command(Command.PLAY)
                .previousNumber(0)
                .resultingNumber(randomWholeNumber)
                .gameId(gameCommand.getGameId())
                .build();

        connectionService.getCurrentSession()
                .orElseThrow(() -> new GameException("No active session found"))
                .send(playEndpointUrl, newCommand);
    }
}
