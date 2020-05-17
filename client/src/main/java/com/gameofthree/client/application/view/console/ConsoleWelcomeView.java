package com.gameofthree.client.application.view.console;

import com.gameofthree.client.application.util.console.ConsoleUtils;
import com.gameofthree.client.application.view.WelcomeView;
import org.springframework.stereotype.Component;

@Component
public class ConsoleWelcomeView implements WelcomeView {
    private String headerTitle;
    private StringBuilder menu;
    private String userPrompt;

    public ConsoleWelcomeView() {
        this.menu = new StringBuilder();
        this.headerTitle = "======= Welcome to Game Of THREE !=======";
        this.menu.append("1) Start new game\n");
        this.menu.append("2) Exit");

        this.userPrompt = "Choose [1-2]: ";
    }


    public void renderScreen() {
        System.out.println(String.format("%s \n%s\n\n%s", headerTitle, menu.toString(), userPrompt));
    }

    @Override
    public Object render() {
        renderScreen();
        int userInput = ConsoleUtils.readNumericInput();
        while (userInput < 1 || userInput > 2) {
            System.out.println("please Choose [1-2]: ");
            userInput = ConsoleUtils.readNumericInput();
        }
        return userInput;
    }
}
