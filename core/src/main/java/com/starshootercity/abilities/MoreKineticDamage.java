package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class MoreKineticDamage implements Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:more_kinetic_damage");
    }

    @Override
    public String description() {
        return "You take more damage from falling and flying into blocks.";
    }

    @Override
    public String title() {
        return "Brittle Bones";
    }
}
