package com.gameofthree.client;

import com.gameofthree.client.application.Application;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@AllArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private Application application;

    @Override
    public void run(String... args) throws Exception {
        application.run();
    }
}
