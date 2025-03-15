package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MoreExhaustion implements Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:more_exhaustion");
    }

    @Override
    public String description() {
        return "You exhaust much quicker than others, thus requiring you to eat more.";
    }

    @Override
    public String title() {
        return "Large Appetite";
    }

    @EventHandler
    public void onEntityExhaustion(EntityExhaustionEvent event) {
        runForAbility(event.getEntity(), player -> event.setExhaustion(event.getExhaustion() * getConfigOption(OriginsReborn.getInstance(), exhaustionMultiplier, ConfigManager.SettingType.FLOAT)));
    }

    private final String exhaustionMultiplier = "exhaustion_multiplier";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), exhaustionMultiplier, Collections.singletonList("Amount to multiply exhaustion by"), ConfigManager.SettingType.FLOAT, 1.6f);
    }
}
