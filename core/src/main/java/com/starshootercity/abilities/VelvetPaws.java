package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.GameEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;

public class VelvetPaws implements Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:velvet_paws");
    }

    @Override
    public String description() {
        return "Your footsteps don't cause any vibrations which could otherwise be picked up by nearby lifeforms.";
    }

    @Override
    public String title() {
        return "Velvet Paws";
    }

    @EventHandler
    public void onGenericGameEvent(GenericGameEvent event) {
        if (event.getEvent() == GameEvent.STEP) {
            runForAbility(event.getEntity(), player -> event.setCancelled(true));
        }
    }
}
