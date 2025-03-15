package com.starshootercity.abilities;

import com.starshootercity.util.ShortcutUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class SlowFalling implements Listener, VisibleAbility {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        runForAbility(event.getPlayer(), player -> {
            if (player.isSneaking()) {
                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            } else player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, ShortcutUtils.infiniteDuration(), 0, false, false));
        });
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:slow_falling");
    }

    @Override
    public String description() {
        return "You fall as gently to the ground as a feather would, unless you sneak.";
    }

    @Override
    public String title() {
        return "Featherweight";
    }
}
