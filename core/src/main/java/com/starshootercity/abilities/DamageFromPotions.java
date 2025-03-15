package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DamageFromPotions implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:damage_from_potions");
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        runForAbility(event.getPlayer(), player -> {
            if (event.getItem().getType() == Material.POTION) {
                OriginsReborn.getNMSInvoker().dealFreezeDamage(player, getConfigOption(OriginsReborn.getInstance(), damageAmount, ConfigManager.SettingType.INTEGER));
            }
        });
    }

    private final String damageAmount = "damage_amount";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), damageAmount, Collections.singletonList("Amount of damage the player should take when drinking a potion"), ConfigManager.SettingType.INTEGER, 2);
    }
}
