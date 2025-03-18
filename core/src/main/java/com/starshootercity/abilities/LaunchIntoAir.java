package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.abilities.types.CooldownAbility;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class LaunchIntoAir implements Listener, CooldownAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:launch_into_air");
    }

    @Override
    public String description() {
        return "Every 30 seconds, you are able to launch about 20 blocks up into the air.";
    }

    @Override
    public String title() {
        return "Gift of the Winds";
    }

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        runForAbility(event.getPlayer(), player -> {
            if (player.isGliding()) {
                if (hasCooldown(player)) return;
                setCooldown(player);
                player.setVelocity(player.getVelocity().add(new Vector(0, getConfigOption(OriginsReborn.getInstance(), launchStrength, ConfigManager.SettingType.FLOAT), 0)));
            }
        });
    }

    @Override
    public Cooldowns.CooldownInfo getCooldownInfo() {
        return new Cooldowns.CooldownInfo(600, "launch");
    }

    private final String launchStrength = "launch_strength";

    @Override
    public void initialize(JavaPlugin plugin) {
        registerConfigOption(OriginsReborn.getInstance(), launchStrength, Collections.singletonList("How strong the launch effect should be"), ConfigManager.SettingType.FLOAT, 2f);
    }
}
