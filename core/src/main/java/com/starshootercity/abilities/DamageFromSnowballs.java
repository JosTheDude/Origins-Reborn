package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DamageFromSnowballs implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:damage_from_snowballs");
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.SNOWBALL) return;
        runForAbility(event.getHitEntity(), player -> {
            OriginsReborn.getNMSInvoker().dealFreezeDamage(player, getConfigOption(OriginsReborn.getInstance(), damageAmount, ConfigManager.SettingType.INTEGER));
            Vector vector = event.getEntity().getLocation().getDirection();

            if (getConfigOption(OriginsReborn.getInstance(), knockback, ConfigManager.SettingType.BOOLEAN)) {
                OriginsReborn.getNMSInvoker().knockback(player, 0.5, -vector.getX(), -vector.getZ());
            }
        });
    }

    private final String damageAmount = "damage_amount";
    private final String knockback = "knockback";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), damageAmount, Collections.singletonList("Amount of damage the player should take when hit with a snowball"), ConfigManager.SettingType.INTEGER, 3);
        registerConfigOption(OriginsReborn.getInstance(), knockback, Collections.singletonList("Whether snowballs should deal knockback too"), ConfigManager.SettingType.BOOLEAN, true);
    }
}
