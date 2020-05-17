package com.gameofthree.client.application;

import com.gameofthree.client.application.exception.GameException;
import com.gameofthree.client.application.service.GameStartupService;
import com.gameofthree.client.application.service.HomeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
public class ConsoleApplication implements Application {
    private final HomeService homeService;
    private final GameStartupService gameStartupService;

    @Override
    public void run() throws ExecutionException, InterruptedException {
        boolean isRunning = true;
        while (isRunning) {
            int userInput = homeService.showMenu();
            switch (userInput) {
                case 1:
                    try {
                        gameStartupService.startNewGame();
                    } catch (GameException e) {
                        isRunning = false;
                    }
                    break;

                case 2:
                    isRunning = false;
                    break;
            }
        }
    }
}
