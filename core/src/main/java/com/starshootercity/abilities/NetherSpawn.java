package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.DefaultSpawnAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherSpawn implements DefaultSpawnAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:nether_spawn");
    }

    @Override
    public @Nullable World getWorld() {
        String nether = OriginsReborn.getInstance().getConfig().getString("worlds.world_nether");
        if (nether == null) {
            nether = "world_nether";
            OriginsReborn.getInstance().getConfig().set("worlds.world_nether", "world_nether");
            OriginsReborn.getInstance().saveConfig();
        }
        return Bukkit.getWorld(nether);
    }

    @Override
    public String description() {
        return "Your natural spawn will be in the Nether.";
    }

    @Override
    public String title() {
        return "Nether Inhabitant";
    }
}
