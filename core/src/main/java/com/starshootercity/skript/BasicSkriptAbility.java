package com.starshootercity.skript;

import com.starshootercity.abilities.types.Ability;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class BasicSkriptAbility implements Ability {

    private final Key key;

    public BasicSkriptAbility(Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key getKey() {
        return key;
    }
}
