package com.gameofthree.client.application.view.console;

import com.gameofthree.client.application.util.console.ConsoleUtils;
import com.gameofthree.client.application.view.RegisterPlayerView;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsoleRegisterPlayerView implements RegisterPlayerView {
    private String enterNamePrompt;
    private String chooseModePrompt;

    public ConsoleRegisterPlayerView() {
        this.enterNamePrompt = "Please enter your nickname: ";
        this.chooseModePrompt = "Please choose your play mode (A/m): \nA: Auto\nM: Manual";
    }

    @Override
    public Object render() {
        System.out.println(enterNamePrompt);
        String nameInput = ConsoleUtils.readAlphaInput(false);

        System.out.println(chooseModePrompt);
        String modeInput;
        // keep prompt user if he didn't enter either A/a/M/m
        do {
            modeInput = ConsoleUtils.readAlphaInput(true);
        } while (!modeInput.equalsIgnoreCase("A") && !modeInput.equalsIgnoreCase("M"));
        return Map.of("nickName", nameInput, "playMode", modeInput);
    }
}
