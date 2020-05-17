package com.gameofthree.client.application.view.console;

import com.gameofthree.client.application.util.console.ConsoleUtils;
import com.gameofthree.client.application.view.GameIdleView;
import org.springframework.stereotype.Component;

@Component
public class ConsoleGameIdleView implements GameIdleView {
    private String idleMessage;
    private String userPrompt;
    private String screen;

    public ConsoleGameIdleView() {
        this.idleMessage = "Please wait for your opponent to join !";
        this.userPrompt = "(quit game? type exit)";
    }

    @Override
    public Object render() {
        this.screen = String.format("%s \n\n%s", idleMessage, userPrompt);
        System.out.println(screen);
        return ConsoleUtils.readAlphaInput(true);
    }

    @Override
    public Object render(String input) {
        this.screen = String.format("Please wait your opponent %s to start the game \n\n%s", input, userPrompt);
        System.out.println(screen);
        return ConsoleUtils.readAlphaInput(true);
    }

    @Override
    public void renderPressEnterKey() {
        System.out.println("\n\n\n\nPress ENTER key to continue");
        ConsoleUtils.enterKeyInput();
    }
}
