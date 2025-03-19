package com.starshootercity.abilities;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.ListenerAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Conduit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class ConduitPowerOnLand implements ListenerAbility, Listener {

    @Override
    public boolean shouldRegisterEvents() {
        return !getConfigOption(forcedCompatibility, ConfigManager.SettingType.BOOLEAN);
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:conduit_power_on_land");
    }

    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            runForAbility(p, player -> OriginsReborn.getNMSInvoker().setTouchingWater(player));
        }
    }

    private final PotionEffect conduitPower = new PotionEffect(PotionEffectType.CONDUIT_POWER, 260, 0, true, true, true);
    private final Predicate<Block> isConduit = block -> block.getType().equals(Material.CONDUIT);

    private final String forcedCompatibility = "forced_compatibility";

    @Override
    public void initialize(JavaPlugin plugin) {
        registerConfigOption(OriginsReborn.getInstance(), forcedCompatibility, List.of("Whether to force compatibility with other abilities", "Less efficient, but prevents it breaking if a player has both this and an ability like origins:water_vulnerability"), ConfigManager.SettingType.BOOLEAN, false);

        if (!getConfigOption(OriginsReborn.getInstance(), forcedCompatibility, ConfigManager.SettingType.BOOLEAN)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Player> players = new HashSet<>();
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        Collection<BlockState> conduits = chunk.getTileEntities(isConduit, true);
                        for (BlockState state : conduits) {
                            int radius = OriginsReborn.getNMSInvoker().getConduitRange((Conduit) state);
                            players.addAll(state.getLocation().getNearbyPlayers(radius));
                        }
                    }
                }

                for (Player player : players.stream().filter(player -> hasAbility(player) && !player.isInWater()).toList()) {
                    player.addPotionEffect(conduitPower);
                }
            }
        }.runTaskTimer(OriginsReborn.getInstance(), 0, 20);
    }
}