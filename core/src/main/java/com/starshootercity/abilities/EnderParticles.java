package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class EnderParticles implements ParticleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:ender_particles");
    }

    @Override
    public Particle getParticle() {
        return Particle.PORTAL;
    }

    @Override
    public int getFrequency() {
        return getConfigOption(OriginsReborn.getInstance(), frequency, ConfigManager.SettingType.INTEGER);
    }

    private final String frequency = "frequency";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), frequency, Collections.singletonList("How often (in ticks) the particles should appear"), ConfigManager.SettingType.INTEGER, 4);
    }
}
