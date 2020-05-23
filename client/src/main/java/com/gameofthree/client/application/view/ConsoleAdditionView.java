package com.gameofthree.client.application.view;

import com.gameofthree.client.application.util.console.ConsoleUtils;
import org.springframework.stereotype.Component;

@Component
public class ConsoleAdditionView implements AdditionView {

    @Override
    public Object render() {
        System.out.println("Please choose your addition move from these values [-1, 0, 1]");
        return ConsoleUtils.readNumericInput();
    }
}
