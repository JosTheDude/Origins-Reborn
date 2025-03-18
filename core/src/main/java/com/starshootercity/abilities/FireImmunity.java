package com.starshootercity.abilities;

import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FireImmunity implements Listener, VisibleAbility {
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (Set.of(
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.FIRE_TICK,
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.HOT_FLOOR
            ).contains(event.getCause())) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:fire_immunity");
    }

    @Override
    public String description() {
        return "You are immune to all types of fire damage.";
    }

    @Override
    public String title() {
        return "Fire Immunity";
    }
}
