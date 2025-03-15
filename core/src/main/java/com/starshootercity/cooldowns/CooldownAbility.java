package com.starshootercity.cooldowns;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.Ability;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

@SuppressWarnings("unused") // Some functions here are unused but may be useful in addons
public interface CooldownAbility extends Ability {

    String COOLDOWN = "cooldown-duration";

    default NamespacedKey getCooldownKey() {
        return new NamespacedKey(OriginsReborn.getInstance(), getKey().asString().replace(":", "-"));
    }

    default void setCooldown(Player player) {
        if (OriginsReborn.getInstance().getConfig().getBoolean("cooldowns.disable-all-cooldowns")) return;
        OriginsReborn.getCooldowns().setCooldown(player, getCooldownKey());
    }

    default void setCooldown(Player player, int amount) {
        if (OriginsReborn.getInstance().getConfig().getBoolean("cooldowns.disable-all-cooldowns")) return;
        OriginsReborn.getCooldowns().setCooldown(player, getCooldownKey(), amount);
    }

    default boolean hasCooldown(Player player) {
        if (OriginsReborn.getInstance().getConfig().getBoolean("cooldowns.disable-all-cooldowns")) return false;
        return OriginsReborn.getCooldowns().hasCooldown(player, getCooldownKey());
    }

    default long getCooldown(Player player) {
        return OriginsReborn.getCooldowns().getCooldown(player, getCooldownKey());
    }

    Cooldowns.CooldownInfo getCooldownInfo();

    default void setupCooldownConfig(JavaPlugin instance) {
        Cooldowns.CooldownInfo info = getCooldownInfo();
        if (!info.isStatic()) {
            registerConfigOption(COOLDOWN, Collections.singletonList("The duration of the cooldown (in ticks)"), ConfigManager.SettingType.INTEGER, info.getCooldownTime());
            int i = getConfigOption(COOLDOWN, ConfigManager.SettingType.INTEGER);
            info.setCooldownTime(i);
        }
        OriginsReborn.getCooldowns().registerCooldown(instance, getCooldownKey(), getCooldownInfo());
    }
}
