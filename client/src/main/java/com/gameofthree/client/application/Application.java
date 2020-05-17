package com.gameofthree.client.application;

import java.util.concurrent.ExecutionException;

public interface Application {
    void run() throws ExecutionException, InterruptedException;
}
