package com.gameofthree.client.application.util.console;

import java.util.Objects;
import java.util.Scanner;

public class ConsoleUtils {

    private ConsoleUtils() {
    }

    public static String readAlphaNumericInput(boolean silent) {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        if (!userInput.matches("[A-Za-z0-9 ]+") && !userInput.equals("")) {
            if (!silent) {
                System.out.println("please enter alphanumeric letters");
            }
            userInput = readAlphaNumericInput(silent);
        }
        return userInput;
    }

    public static String readAlphaInput(boolean silent) {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        if (!userInput.matches("[A-Za-z ]+") && !userInput.equals("")) {
            if (!silent) {
                System.out.println("please enter alphabet letters only");
            }
            userInput = readAlphaInput(silent);
        }
        return userInput;
    }

    public static String anyKeyInput() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public static String enterKeyInput() {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        if (Objects.isNull(userInput) || !userInput.equals("")) {
            userInput = enterKeyInput();
        }
        return userInput;
    }


    public static int readNumericInput() {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        int userChoice = 0;
        try {
            if (userInput.equals(""))
                userChoice = -15;
            else
                userChoice = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("please enter a valid number");
            userChoice = readNumericInput();
        }
        return userChoice;
    }
}
