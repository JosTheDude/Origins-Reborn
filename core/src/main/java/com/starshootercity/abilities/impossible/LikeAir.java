package com.starshootercity.abilities.impossible;

import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class LikeAir implements VisibleAbility, Listener {

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:like_air");
    }

    @Override
    public String description() {
        return "Modifiers to your walking speed also apply while you're airborne.";
    }

    @Override
    public String title() {
        return "Like Air";
    }
}
