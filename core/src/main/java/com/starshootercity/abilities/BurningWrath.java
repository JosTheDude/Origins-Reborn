package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BurningWrath implements Listener, VisibleAbility {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        runForAbility(event.getDamager(), player -> {
            if (player.getFireTicks() > 0) event.setDamage(event.getDamage() + getConfigOption(OriginsReborn.getInstance(), damageIncrease, ConfigManager.SettingType.INTEGER));
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:burning_wrath");
    }

    @Override
    public String description() {
        return "When on fire, you deal additional damage with your attacks.";
    }

    @Override
    public String title() {
        return "Burning Wrath";
    }

    private final String damageIncrease = "damage_increase";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), damageIncrease, Collections.singletonList("How much to increase damage dealt when on fire"), ConfigManager.SettingType.INTEGER, 3);
    }
}
