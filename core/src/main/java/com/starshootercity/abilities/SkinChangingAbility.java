package com.starshootercity.abilities;

import com.starshootercity.util.SkinManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.awt.image.BufferedImage;

public interface SkinChangingAbility extends Ability, Listener {
    void modifySkin(BufferedImage image, Player player);

    default int getPriority(Player player) {
        return 0;
    }

    default boolean forceEnabled() {
        return false;
    }

    default boolean shouldApply(Player player) {
        return true;
    }

    default void forceUpdate(Player player) {
        SkinManager.updateSkin(player, true);
    }
}
