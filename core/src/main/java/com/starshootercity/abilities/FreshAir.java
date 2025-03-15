package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class FreshAir implements Listener, VisibleAbility {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!event.getAction().isRightClick()) return;
        if (
                event.getPlayer().isSneaking() &&
                        event.getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR &&
                        event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR
        ) return;
        if (Tag.BEDS.isTagged(event.getClickedBlock().getType())) {
            runForAbility(event.getPlayer(), player -> {
                if (event.getClickedBlock().getY() < getConfigOption(OriginsReborn.getInstance(), minHeight, ConfigManager.SettingType.INTEGER)) {
                    String overworld = OriginsReborn.getInstance().getConfig().getString("worlds.world");
                    if (overworld == null) {
                        overworld = "world";
                        OriginsReborn.getInstance().getConfig().set("worlds.world", "world");
                        OriginsReborn.getInstance().saveConfig();
                    }
                    boolean isInOverworld = player.getWorld() == Bukkit.getWorld(overworld);

                    if (!isInOverworld) return;
                    if (event.getClickedBlock().getWorld().isDayTime() && event.getClickedBlock().getWorld().isClearWeather()) return;
                    event.setCancelled(true);
                    player.swingMainHand();
                    player.sendActionBar(Component.text(translate("avian_sleep_fail")));
                }
            });
        }
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:fresh_air");
    }

    @Override
    public String description() {
        return "When sleeping, your bed needs to be at an altitude of at least %s blocks, so you can breathe fresh air.";
    }

    @Override
    public String modifyDescription(String description) {
        return description.formatted(getConfigOption(OriginsReborn.getInstance(), minHeight, ConfigManager.SettingType.INTEGER));
    }

    @Override
    public String title() {
        return "Fresh Air";
    }

    public static String minHeight = "minimum_altitude";

    @Override
    public void initialize() {
        registerTranslation("avian_sleep_fail", "You need fresh air to sleep");
        registerConfigOption(OriginsReborn.getInstance(), minHeight, Collections.singletonList("Minimum altitude the player can sleep at"), ConfigManager.SettingType.INTEGER, 86);
    }
}
