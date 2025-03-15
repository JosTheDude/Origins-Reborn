package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class FallImmunity implements Listener, VisibleAbility {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:fall_immunity");
    }

    @Override
    public String description() {
        return "You never take fall damage, no matter from which height you fall.";
    }

    @Override
    public String title() {
        return "Acrobatics";
    }
}
