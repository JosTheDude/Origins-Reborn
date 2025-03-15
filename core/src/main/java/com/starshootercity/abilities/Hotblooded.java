package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Hotblooded implements Listener, VisibleAbility {
    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (event.getNewEffect() != null) {
                if (event.getNewEffect().getType().equals(PotionEffectType.POISON) || event.getNewEffect().getType().equals(PotionEffectType.HUNGER)) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:hotblooded");
    }

    @Override
    public String description() {
        return "Due to your hot body, venoms burn up, making you immune to poison and hunger status effects.";
    }

    @Override
    public String title() {
        return "Hotblooded";
    }
}
