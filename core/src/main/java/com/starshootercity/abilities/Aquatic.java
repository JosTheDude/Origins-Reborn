package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class Aquatic implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:aquatic");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (event.getDamager() instanceof Trident trident) {
                event.setDamage(event.getDamage() + trident.getItem().getEnchantmentLevel(Enchantment.IMPALING) * getConfigOption(OriginsReborn.getInstance(), damageMultiplier, ConfigManager.SettingType.FLOAT));
            } else if (event.getDamager() instanceof LivingEntity entity) {
                if (entity.getEquipment() == null) return;
                event.setDamage(event.getDamage() + entity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.IMPALING) * getConfigOption(OriginsReborn.getInstance(), damageMultiplier, ConfigManager.SettingType.FLOAT));
            }
        });
    }

    private final String damageMultiplier = "impaling_damage_increase";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), damageMultiplier, Collections.singletonList("Amount to increase damage by per level of Impaling"), ConfigManager.SettingType.FLOAT, 2.5f);
    }
}
