package com.gameofthree.client.application.view.console;

import com.gameofthree.client.application.view.MessagesView;
import org.springframework.stereotype.Component;

@Component
public class ConsoleMessageView implements MessagesView {

    @Override
    public Object render(String input) {
        System.out.println(input);
        return input;
    }

    @Override
    public void renderAfterNewLine(String input) {
        System.out.println("\n" + input);
    }
}
