package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.service.HomeService;
import com.gameofthree.client.application.view.WelcomeView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultHomeService implements HomeService {
    private final WelcomeView welcomeView;

    @Override
    public int showMenu() {
        // tried to decouple the way of console representation from code logic as much as possible
        return (int) welcomeView.render();
    }
}
