package com.starshootercity.abilities;

public interface AsyncRepeatingAbility {
    int interval();

    void run();

    void start();
}
