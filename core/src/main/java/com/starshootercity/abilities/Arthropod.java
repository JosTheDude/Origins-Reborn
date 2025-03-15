package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;

public class Arthropod implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:arthropod");
    }

    private final Random random = new Random();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity entity) {
            if (entity.getEquipment() == null) return;
            if (entity.getEquipment().getItemInMainHand().containsEnchantment(OriginsReborn.getNMSInvoker().getBaneOfArthropodsEnchantment())) {
                runForAbility(event.getEntity(), player -> {
                    int level = entity.getEquipment().getItemInMainHand().getEnchantmentLevel(OriginsReborn.getNMSInvoker().getBaneOfArthropodsEnchantment());
                    event.setDamage(event.getDamage() + 1.25 * level);
                    if (getConfigOption(OriginsReborn.getInstance(), applySlowness, ConfigManager.SettingType.BOOLEAN)) {
                        player.addPotionEffect(new PotionEffect(OriginsReborn.getNMSInvoker().getSlownessEffect(), (int) (20 * random.nextDouble(1, 1 + (0.5 * level))), 3, false, true));
                    }
                });
            }
        }
    }

    private final String applySlowness = "apply_slowness";

    @Override
    public void initialize() {
        registerConfigOption(OriginsReborn.getInstance(), applySlowness, Collections.singletonList("Apply Slowness when using Bane of Arthropods on players with the Arthropod ability"), ConfigManager.SettingType.BOOLEAN, true);
    }
}
