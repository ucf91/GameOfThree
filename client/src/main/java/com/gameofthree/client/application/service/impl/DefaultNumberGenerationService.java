package com.gameofthree.client.application.service.impl;

import com.gameofthree.client.application.service.NumberGenerationService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DefaultNumberGenerationService implements NumberGenerationService {
    public static final Integer RANDOM_SEED_UPPER_BOUND = 100;
    public static final Integer RANDOM_LOWER_BOUND = 50;
    private Random random;

    public DefaultNumberGenerationService() {
        random = new Random();
    }

    @Override
    public int generateRandomWholeNumber() {
        return random.nextInt(RANDOM_SEED_UPPER_BOUND) + RANDOM_LOWER_BOUND;// minimum starting number is 50
    }

    @Override
    public int generateAdditionNumber() {
        // generate random number from -1 to 1
        return random.nextInt(3) - 1;
    }
}
