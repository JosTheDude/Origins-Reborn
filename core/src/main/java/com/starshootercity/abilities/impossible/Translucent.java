package com.starshootercity.abilities.impossible;

import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Translucent implements VisibleAbility {

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:translucent");
    }

    @Override
    public String description() {
        return "Your skin is translucent.";
    }

    @Override
    public @NotNull String title() {
        return "Translucent";
    }
}
