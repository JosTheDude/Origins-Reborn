package com.starshootercity.papi;

import com.starshootercity.Origin;
import com.starshootercity.OriginSwapper;
import com.starshootercity.OriginsReborn;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayerPlaceholderExpansion extends PlaceholderExpansion {

    private final String layer;

    public LayerPlaceholderExpansion(String layer) {
        this.layer = layer;
    }

    @Override
    public @NotNull String getIdentifier() {
        return layer;
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
        Origin origin = OriginSwapper.getOrigin(player, layer);
        if (origin == null) return "";
        return origin.getName();
    }
}
