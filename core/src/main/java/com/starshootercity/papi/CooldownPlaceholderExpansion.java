package com.starshootercity.papi;

import com.starshootercity.OriginsReborn;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CooldownPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "cooldown";
    }

    @Override
    public @NotNull String getAuthor() {
        return "cometcake575";
    }

    @Override
    public @NotNull String getVersion() {
        return OriginsReborn.getInstance().getConfig().getString("config-version", "unknown");
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return "This placeholder is incomplete!";
    }
}
