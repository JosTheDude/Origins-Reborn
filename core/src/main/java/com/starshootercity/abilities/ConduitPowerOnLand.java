package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Conduit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConduitPowerOnLand implements Ability {

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:conduit_power_on_land");
    }

    private final PotionEffect conduitPower = new PotionEffect(PotionEffectType.CONDUIT_POWER, 260, 0, true, true, true);

    @Override
    public void initialize() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Player> players = new HashSet<>();
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        Collection<BlockState> conduits = chunk.getTileEntities(block -> block.getType().equals(Material.CONDUIT), true);
                        for (BlockState state : conduits) {
                            int radius = OriginsReborn.getNMSInvoker().getConduitRange((Conduit) state);
                            players.addAll(state.getLocation().getNearbyPlayers(radius));
                        }
                    }
                }

                for (Player player : players.stream().filter(player -> hasAbility(player) && !player.isInWater()).toList()) player.addPotionEffect(conduitPower);
            }
        }.runTaskTimer(OriginsReborn.getInstance(), 0, 20);
    }
}
