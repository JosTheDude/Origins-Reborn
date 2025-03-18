package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Carnivore implements VisibleAbility, Listener {

    List<Material> meat;

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) return;
        runForAbility(event.getPlayer(), player -> {
            if (!meat.contains(event.getItem().getType())) {
                if (OriginsReborn.getNMSInvoker().getOminousBottle() != null) {
                    if (event.getItem().getType() == OriginsReborn.getNMSInvoker().getOminousBottle()) return;
                }
                event.setCancelled(true);

                if (getConfigOption(OriginsReborn.getInstance(), poison, ConfigManager.SettingType.BOOLEAN)) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 1, false, true));
                }
            }
        });
    }

    @Override
    public String description() {
        return "Your diet is restricted to meat, you can't eat vegetables.";
    }

    @Override
    public String title() {
        return "Carnivore";
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:carnivore");
    }

    private final String poison = "consume_and_poison";

    @Override
    public void initialize(JavaPlugin plugin) {
        registerConfigOption(OriginsReborn.getInstance(), poison, Collections.singletonList("Poison the player when consuming non-meat rather than preventing them from eating it"), ConfigManager.SettingType.BOOLEAN, false);

        String meatContents = "meat";
        registerConfigOption(OriginsReborn.getInstance(), meatContents, Collections.singletonList("Items that count as meat"), ConfigManager.SettingType.MATERIAL_LIST, List.of(
                Material.PORKCHOP,
                Material.COOKED_PORKCHOP,
                Material.BEEF,
                Material.COOKED_BEEF,
                Material.CHICKEN,
                Material.COOKED_CHICKEN,
                Material.RABBIT,
                Material.COOKED_RABBIT,
                Material.MUTTON,
                Material.COOKED_MUTTON,
                Material.RABBIT_STEW,
                Material.COOKED_COD,
                Material.TROPICAL_FISH,
                Material.SALMON,
                Material.COOKED_SALMON,
                Material.PUFFERFISH
        ));

        meat = getConfigOption(OriginsReborn.getInstance(), meatContents, ConfigManager.SettingType.MATERIAL_LIST);
    }
}
