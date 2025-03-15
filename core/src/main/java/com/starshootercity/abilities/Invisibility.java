package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Invisibility implements DependantAbility, VisibilityChangingAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:invisibility");
    }

    @Override
    public @NotNull Key getDependencyKey() {
        return Key.key("origins:phantomize");
    }

    @Override
    public String description() {
        return "While phantomized, you are invisible.";
    }

    @Override
    public String title() {
        return "Invisibility";
    }

    @Override
    public boolean isInvisible(Player player) {
        return getDependency().isEnabled(player);
    }
}
