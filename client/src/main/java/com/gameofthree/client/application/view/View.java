package com.gameofthree.client.application.view;

public interface View {
    default Object render() {
        return null;
    }

    default Object render(String input) {
        return null;
    }
}
