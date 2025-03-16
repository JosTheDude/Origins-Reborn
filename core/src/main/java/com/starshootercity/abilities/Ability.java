package com.starshootercity.abilities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.starshootercity.*;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.util.WorldGuardHook;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.starshootercity.abilities.AbilityRegister.abilityMap;

public interface Ability {
    @NotNull Key getKey();

    default void runForAbility(Entity entity, @NotNull AbilityRunner runner) {
        runForAbility(entity, runner, null);
    }

    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable AbilityRunner other) {
        if (entity instanceof Player player) {
            if (hasAbility(player)) {
                if (has != null) has.run(player);
            } else if (other != null) other.run(player);
        }
    }

    default boolean hasAbility(Player player) {

        if (OriginsReborn.getInstance().getConfig().getStringList("worlds.disabled-worlds").contains(player.getWorld().getName())) return false;

        for (OriginsAddon.KeyStateGetter keyStateGetter : AddonLoader.abilityOverrideChecks) {
            OriginsAddon.State state = keyStateGetter.get(player, getKey());
            if (state == OriginsAddon.State.DENY) return false;
            else if (state == OriginsAddon.State.ALLOW) return true;
        }

        if (OriginsReborn.isWorldGuardHookInitialized()) {
            if (WorldGuardHook.isAbilityDisabled(player.getLocation(), this)) return false;

            ConfigurationSection section = OriginsReborn.getInstance().getConfig().getConfigurationSection("prevent-abilities-in");
                Location loc = BukkitAdapter.adapt(player.getLocation());
            if (section != null) {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(loc);
                for (ProtectedRegion region : set) {
                    for (String sectionKey : section.getKeys(false)) {
                        if (!section.getStringList(sectionKey).contains(getKey().toString()) && !section.getStringList(sectionKey).contains("all"))
                            continue;
                        if (region.getId().equalsIgnoreCase(sectionKey)) {
                            return false;
                        }
                    }
                }
            }
        }

        List<Origin> origins = OriginSwapper.getOrigins(player);
        boolean hasAbility = origins.stream().anyMatch(origin -> origin.hasAbility(getKey()));

        for (MultiAbility multiAbility : AbilityRegister.multiAbilityMap.getOrDefault(getKey(), Collections.emptyList())) {
            if (multiAbility.hasAbility(player)) hasAbility = true;
        }
        if (abilityMap.get(getKey()) instanceof DependantAbility dependantAbility) {
            return hasAbility && ((dependantAbility.getDependencyType() == DependantAbility.DependencyType.REGULAR) == dependantAbility.getDependency().isEnabled(player));
        }
        return hasAbility;
    }

    interface AbilityRunner {
        void run(Player player);
    }

    /**
     * Used for things like initializing config options or translatable pieces of text
     * @see Ability#registerTranslation(String, String)
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default void initialize() {

    }

    /**
     * Registers a translation in the translations.yml file associated with this ability
     * @param key Key of the translation
     * @param def Default value for the translatable ability
     * @see Ability#translate(String)
     */
    default void registerTranslation(String key, String def) {
        Translator.registerTranslation("abilities." + getKey() + "." + key, def);
    }


    /**
     * Retrieves a translation in the translations.yml file associated with this ability
     * @param key Key of the translation
     * @see Ability#registerTranslation(String, String)
     */
    default String translate(String key) {
        return Translator.translate("abilities." + getKey() + "." + key);
    }

    /**
     * Registers a config option with this ability under a certain Origins-Reborn addon
     * @param addon The addon to register the config option under
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    default <T> void registerConfigOption(OriginsAddon addon, String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
        AbilityRegister.registerConfigOption(addon, this, settingType, path, comments, defaultValue);
    }

    /**
     * Registers a config option with this ability under a certain Origins-Reborn addon
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    default <T> void registerConfigOption(String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
        registerConfigOption(AbilityRegister.pluginMap.get(getKey()), path, comments, settingType, defaultValue);
    }

    /**
     * Gets a config value under a specified plugin
     * @param addon The addon this config option is registered under
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(OriginsAddon addon, String path, ConfigManager.SettingType<T> settingType) {
        return AbilityRegister.getConfigOption(addon, this, settingType, path);
    }

    /**
     * Gets a config value under the plugin this ability was initialized by
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(String path, ConfigManager.SettingType<T> settingType) {
        return getConfigOption(AbilityRegister.pluginMap.get(getKey()), path, settingType);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see Ability#getConfigOption(String, ConfigManager.SettingType)
     */
    @Deprecated(forRemoval = true)
    default <T> T getConfigOption(String path, SettingType<T> settingType) {
        return getConfigOption(AbilityRegister.pluginMap.get(getKey()), path, settingType);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    @Deprecated(forRemoval = true)
    default <T> T getConfigOption(OriginsAddon addon, String path, SettingType<T> settingType) {
        return AbilityRegister.getConfigOption(addon, this, settingType, path);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see Ability#registerConfigOption(String, List, ConfigManager.SettingType, Object)
     */
    @Deprecated(forRemoval = true)
    default <T> void registerConfigOption(String path, List<String> comments, SettingType<T> settingType, T defaultValue) {
        registerConfigOption(AbilityRegister.pluginMap.get(getKey()), path, comments, settingType, defaultValue);
    }

    /**
     * @deprecated SettingType is now contained in the ConfigManager
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
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
