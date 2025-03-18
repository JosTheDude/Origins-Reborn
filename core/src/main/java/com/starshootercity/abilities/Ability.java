package com.starshootercity.abilities;

import com.starshootercity.OriginsAddon;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
 * @see com.starshootercity.abilities.types.Ability
 */
@Deprecated
public interface Ability extends com.starshootercity.abilities.types.Ability {

    @Override
    default void initialize(JavaPlugin plugin) {
        initialize();
    }

    @Deprecated
    default void initialize() {

    }

    @Deprecated(forRemoval = true)
    default void runForAbility(Entity entity, @NotNull AbilityRunner runner) {
        runForAbility(entity, runner, null);
    }

    @Deprecated(forRemoval = true)
    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable AbilityRunner other) {
        if (entity instanceof Player player) {
            if (hasAbility(player)) {
                if (has != null) has.run(player);
            } else if (other != null) other.run(player);
        }
    }

    @Deprecated(forRemoval = true)
    interface AbilityRunner {
        void run(Player player);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see com.starshootercity.abilities.types.Ability#getConfigOption(String, ConfigManager.SettingType)
     */
    @Deprecated(forRemoval = true)
    default <T> T getConfigOption(String path, SettingType<T> settingType) {
        return getConfigOption(com.starshootercity.util.AbilityRegister.pluginMap.get(getKey()), path, settingType);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see com.starshootercity.abilities.types.Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    @Deprecated(forRemoval = true)
    default <T> T getConfigOption(OriginsAddon addon, String path, SettingType<T> settingType) {
        return com.starshootercity.util.AbilityRegister.getConfigOption(addon, this, settingType, path);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see com.starshootercity.abilities.types.Ability#registerConfigOption(String, List, ConfigManager.SettingType, Object)
     */
    @Deprecated(forRemoval = true)
    default <T> void registerConfigOption(String path, List<String> comments, SettingType<T> settingType, T defaultValue) {
        registerConfigOption(com.starshootercity.util.AbilityRegister.pluginMap.get(getKey()), path, comments, settingType, defaultValue);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see com.starshootercity.abilities.types.Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    @Deprecated(forRemoval = true)
    default <T> void registerConfigOption(OriginsAddon addon, String path, List<String> comments, SettingType<T> settingType, T defaultValue) {
        AbilityRegister.registerConfigOption(addon, this, settingType, path, comments, defaultValue);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see ConfigManager.SettingType
     */
    @Deprecated(forRemoval = true)
    interface SettingType<T> extends ConfigManager.SettingType<T> {

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#STRING
         */
        @Deprecated(forRemoval = true)
        SettingType<String> STRING = ConfigurationSection::getString;

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#STRING_LIST
         */
        @Deprecated(forRemoval = true)
        SettingType<List<String>> STRING_LIST = ConfigurationSection::getStringList;

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#MATERIAL_LIST
         */
        @Deprecated(forRemoval = true)
        SettingType<List<Material>> MATERIAL_LIST = new SettingType<>() {
            @Override
            public List<Material> get(ConfigurationSection config, String path) {
                List<Material> result = new ArrayList<>();
                for (String s : config.getStringList(path)) {
                    result.add(Material.matchMaterial(s));
                }
                return result;
            }

            @Override
            public void set(ConfigurationSection config, String path, List<Material> value) {
                List<String> stringValues = new ArrayList<>();
                for (Material material : value) stringValues.add("minecraft:" + material.toString().toLowerCase());
                config.set(path, stringValues);
            }
        };

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#INTEGER
         */
        @Deprecated(forRemoval = true)
        SettingType<Integer> INTEGER = ConfigurationSection::getInt;

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#FLOAT
         */
        @Deprecated(forRemoval = true)
        SettingType<Float> FLOAT = (config, path) -> (float) config.getDouble(path);

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#DOUBLE
         */
        @Deprecated(forRemoval = true)
        SettingType<Double> DOUBLE = ConfigurationSection::getDouble;

        /**
         * @deprecated SettingType is now contained in the ConfigManager
         * @see ConfigManager.SettingType#BOOLEAN
         */
        @Deprecated(forRemoval = true)
        SettingType<Boolean> BOOLEAN = ConfigurationSection::getBoolean;
    }
}
