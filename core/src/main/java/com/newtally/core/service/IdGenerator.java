package com.newtally.core.service;

import java.util.Random;

public class IdGenerator {

    private final Random random = new Random();

    public long nextId() {
        return Math.abs(random.nextLong());
    }
}
