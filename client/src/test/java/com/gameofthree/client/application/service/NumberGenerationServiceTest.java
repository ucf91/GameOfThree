package com.gameofthree.client.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class NumberGenerationServiceTest {

    @Autowired
    NumberGenerationService numberGenerationService;

    @DisplayName("when generate random whole number then the result should be >= 50 and < 150")
    @Test
    public void testGenerateRandomWholeNumber() {
        //given
        //when
        int result = numberGenerationService.generateRandomWholeNumber();

        //then
        assertThat("result should be >= 50", result >= 50, equalTo(true));
        assertThat("result should be < 150", result < 150, equalTo(true));
    }

    @DisplayName("when generate random addition number then the result should be in [-1,0,1]")
    @Test
    public void testGenerateAdditionNumber() {
        //given
        //when
        int result = numberGenerationService.generateAdditionNumber();

        //then
        assertThat("result should be >= -1", result >= -1, equalTo(true));
        assertThat("result should be <= 1 ", result <= 1, equalTo(true));
    }
}
