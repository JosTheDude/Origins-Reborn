package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AerialCombatant implements VisibleAbility, Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        runForAbility(event.getDamager(), player -> {
            if (player.isGliding()) event.setDamage(event.getDamage() * getConfigOption(OriginsReborn.getInstance(), damageMultiplier, ConfigManager.SettingType.FLOAT));
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:aerial_combatant");
    }

    @Override
    public String description() {
        return "You deal substantially more damage while in Elytra flight.";
    }

    @Override
    public String title() {
        return "Aerial Combatant";
    }

    private final String damageMultiplier = "damage_multiplier";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), damageMultiplier, Collections.singletonList("Amount to multiply the damage by when gliding"), ConfigManager.SettingType.FLOAT, 2f);
    }
}
