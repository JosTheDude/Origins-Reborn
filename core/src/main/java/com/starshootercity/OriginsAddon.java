package com.starshootercity;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.events.PlayerSwapOriginEvent;
import com.starshootercity.packetsenders.OriginsRebornResourcePackInfo;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public abstract class OriginsAddon extends JavaPlugin {

    public @Nullable SwapStateGetter shouldOpenSwapMenu() {
        return null;
    }

    public @Nullable SwapStateGetter shouldAllowOriginSwapCommand() {
        return null;
    }

    public @Nullable KeyStateGetter getAbilityOverride() {
        return null;
    }

    public interface SwapStateGetter {
        State get(Player player, PlayerSwapOriginEvent.SwapReason reason);
    }

    public interface KeyStateGetter {
        State get(Player player, Key key);
    }

    @SuppressWarnings("unused")
    public enum State {
        ALLOW,
        DEFAULT,
        DENY
    }

    @Override
    public final void onEnable() {
        onRegister();
        AddonLoader.register(this);

        // Will be removed in the future
        for (Ability ability : getAbilities()) {
            AbilityRegister.registerAbility(ability, this);
        }

        for (Ability ability : getRegisteredAbilities()) {
            AbilityRegister.registerAbility(ability, this);
        }

        if (getResourcePackInfo() != null) PackApplier.addResourcePack(this, getResourcePackInfo());
        afterRegister();
    }

    public @Nullable OriginsRebornResourcePackInfo getResourcePackInfo() {
        return null;
    }

    @Override
    public @NotNull File getFile() {
        return super.getFile();
    }

    public void onRegister() {}

    public void afterRegister() {}

    public abstract @NotNull String getNamespace();

    /**
     * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
     * @return List of abilities the plugin will register
     * @see OriginsAddon#getRegisteredAbilities()
     */
    @Deprecated(forRemoval = true)
    public @NotNull List<com.starshootercity.abilities.Ability> getAbilities() {
        return List.of();
    }

    /**
     * Used to return a list of abilities the plugin should register
     * @return List of abilities the plugin will register
     */
    public @NotNull List<Ability> getRegisteredAbilities() {
        return List.of();
    }
}
