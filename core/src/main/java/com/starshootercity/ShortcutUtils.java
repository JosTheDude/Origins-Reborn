package com.starshootercity;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
  * @deprecated Moved to utils directory
  * @see com.starshootercity.util.ShortcutUtils
 */
@Deprecated(forRemoval = true)
public class ShortcutUtils {

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#giveItemWithDrops(Player, ItemStack...)
     */
    @Deprecated(forRemoval = true)
    public static void giveItemWithDrops(Player player, ItemStack... itemStacks) {
        com.starshootercity.util.ShortcutUtils.giveItemWithDrops(player, itemStacks);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#getLivingDamageSource(EntityDamageByEntityEvent)
     */
    @Deprecated(forRemoval = true)
    public static @Nullable LivingEntity getLivingDamageSource(@NotNull EntityDamageByEntityEvent event) {
        return com.starshootercity.util.ShortcutUtils.getLivingDamageSource(event);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#openJSONFile(File)
     */
    @Deprecated(forRemoval = true)
    public static JSONObject openJSONFile(File file) {
        return com.starshootercity.util.ShortcutUtils.openJSONFile(file);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#isBedrockPlayer(UUID)
     */
    @Deprecated(forRemoval = true)
    public static boolean isBedrockPlayer(UUID uuid) {
        return com.starshootercity.util.ShortcutUtils.isBedrockPlayer(uuid);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#getColored(String)
     */
    @Deprecated(forRemoval = true)
    public static Component getColored(String f) {
        return com.starshootercity.util.ShortcutUtils.getColored(f);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#substringsBetween(String, String, String)
     */
    @Deprecated(forRemoval = true)
    public static List<String> substringsBetween(String s, String start, String end) {
        return com.starshootercity.util.ShortcutUtils.substringsBetween(s, start, end);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#isInfinite(PotionEffect)
     */
    @Deprecated(forRemoval = true)
    public static boolean isInfinite(PotionEffect effect) {
        return com.starshootercity.util.ShortcutUtils.isInfinite(effect);
    }

    /**
     * @deprecated Moved to utils directory
     * @see com.starshootercity.util.ShortcutUtils#infiniteDuration()
     */
    @Deprecated(forRemoval = true)
    public static int infiniteDuration() {
        return com.starshootercity.util.ShortcutUtils.infiniteDuration();
    }
}
