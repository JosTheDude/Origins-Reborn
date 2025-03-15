package com.starshootercity;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Translator {

    /**
     * Returns the width of a character
     * @param character The character to get the width of
     * @return The width of the negative space required to move back to before the character
     */
    public static int getWidth(char character) {
        // Description indent
        if (character == '\uF00A') return 2;
        // Space
        if (character == ' ') return 4;
        for (int i = 2; i < 17; i++) {
            if (fileConfig.getString("character-widths.%s".formatted(i), "").contains(toUnicode(character))) {
                return i;
            }
        }
        return 0;
    }

    private static String toUnicode(char ch) {
        return String.format("\\u%04x", (int) ch);
    }

    private static File file;
    private static FileConfiguration fileConfig;

    /**
     * Registers a translation in translations.yml
     * @param key The key of the translation
     * @param def The default value of the translation
     * @see Translator#translate(String)
     */
    public static void registerTranslation(String key, String def) {
        key = "translations." + key;
        if (!fileConfig.contains(key)) {
            fileConfig.set(key, def);
            try {
                fileConfig.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Map<String, String> cache = new HashMap<>();

    /**
     * Retrieves a translation from translations.yml
     * @param key The key of the translation
     * @return The value of the translation
     */
    public static String translate(String key) {
        return cache.computeIfAbsent(key, s -> fileConfig.getString("translations." + key));
    }

    public static void reloadTranslations() {
        cache.clear();
        try {
            fileConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "translations.yml");

        if (!file.exists()) {
            boolean ignored = file.getParentFile().mkdirs();
            plugin.saveResource("translations.yml", false);
        }

        fileConfig = new YamlConfiguration();

        reloadTranslations();
    }
}
