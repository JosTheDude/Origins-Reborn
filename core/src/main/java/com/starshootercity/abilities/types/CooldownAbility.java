package com.starshootercity.abilities.types;

import com.starshootercity.OriginsReborn;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

@SuppressWarnings("unused")
public interface CooldownAbility extends Ability {

    String COOLDOWN = "cooldown-duration";

    default NamespacedKey getCooldownKey() {
        return new NamespacedKey(OriginsReborn.getInstance(), getKey().asString().replace(":", "-"));
    }

    /**
     * Sets the cooldown of this ability to the default amount
     * @param player The player with the ability
     */
    default void setCooldown(Player player) {
        if (ConfigManager.getConfigValue(ConfigManager.Option.DISABLE_ALL_COOLDOWNS)) return;
        OriginsReborn.getCooldowns().setCooldown(player, getCooldownKey());
    }

    /**
     * Sets the cooldown of this ability to a specified amount
     * @param player The player with the ability
     * @param amount The duration of the cooldown
     */
    default void setCooldown(Player player, int amount) {
        if (ConfigManager.getConfigValue(ConfigManager.Option.DISABLE_ALL_COOLDOWNS)) return;
        OriginsReborn.getCooldowns().setCooldown(player, getCooldownKey(), amount);
    }

    /**
     * @param player The player with the ability
     * @return Whether this ability is on cooldown
     */
    default boolean hasCooldown(Player player) {
        if (ConfigManager.getConfigValue(ConfigManager.Option.DISABLE_ALL_COOLDOWNS)) return false;
        return OriginsReborn.getCooldowns().hasCooldown(player, getCooldownKey());
    }

    /**
     * @param player The player with the ability
     * @return The current cooldown of this ability
     */
    default long getCooldown(Player player) {
        return OriginsReborn.getCooldowns().getCooldown(player, getCooldownKey());
    }

    /**
     * @return CooldownInfo about the ability such as cooldown duration, icon, etc.
     */
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
