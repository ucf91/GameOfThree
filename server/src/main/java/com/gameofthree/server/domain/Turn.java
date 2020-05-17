package com.gameofthree.server.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Turn implements ValueObject {
    private int number;
}
