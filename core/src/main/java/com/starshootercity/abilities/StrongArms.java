package com.starshootercity.abilities;

import com.destroystokyo.paper.MaterialTags;
import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StrongArms implements MultiAbility, Listener, VisibleAbility {

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:strong_arms");
    }

    @Override
    public String description() {
        return "You are strong enough to break natural stones without using a pickaxe.";
    }

    @Override
    public String title() {
        return "Strong Arms";
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of(StrongArmsDrops.strongArmsDrops, StrongArmsBreakSpeed.strongArmsBreakSpeed);
    }

    private static List<Material> naturalStones;

    @Override
    public void initialize() {
        String naturalStone = "natural_stones";
        registerConfigOption(OriginsReborn.getInstance(), naturalStone, Collections.singletonList("Blocks that count as natural stone"), ConfigManager.SettingType.MATERIAL_LIST, List.of(
                Material.STONE,
                Material.TUFF,
                Material.GRANITE,
                Material.DIORITE,
                Material.ANDESITE,
                Material.SANDSTONE,
                Material.SMOOTH_SANDSTONE,
                Material.RED_SANDSTONE,
                Material.SMOOTH_RED_SANDSTONE,
                Material.DEEPSLATE,
                Material.BLACKSTONE,
                Material.NETHERRACK
        ));

        naturalStones = getConfigOption(OriginsReborn.getInstance(), naturalStone, ConfigManager.SettingType.MATERIAL_LIST);
    }

    public static class StrongArmsDrops implements Ability, Listener {
        public static StrongArmsDrops strongArmsDrops = new StrongArmsDrops();

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            runForAbility(event.getPlayer(), player -> {
                if (naturalStones.contains(event.getBlock().getType())) {
                    if (!MaterialTags.PICKAXES.isTagged(player.getInventory().getItemInMainHand().getType())) {
                        event.setCancelled(true);
                        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
                        item.addUnsafeEnchantments(player.getInventory().getItemInMainHand().getEnchantments());
                        event.getBlock().breakNaturally(item, event instanceof BreakSpeedModifierAbility.ModifiedBlockBreakEvent);
                    }
                }
            });
        }

        @Override
        public @NotNull Key getKey() {
            return Key.key("origins:strong_arms_drops");
        }
    }

    public static class StrongArmsBreakSpeed implements BreakSpeedModifierAbility, Listener {
        public static StrongArmsBreakSpeed strongArmsBreakSpeed = new StrongArmsBreakSpeed();

        @Override
        public @NotNull Key getKey() {
            return Key.key("origins:strong_arms_break_speed");
        }

        @Override
        @SuppressWarnings("deprecation")
        public BlockMiningContext provideContextFor(Player player) {
            boolean aquaAffinity = false;
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null) {
                if (helmet.containsEnchantment(OriginsReborn.getNMSInvoker().getAquaAffinityEnchantment())) aquaAffinity = true;
            }
            return new BlockMiningContext(
                    new ItemStack(Material.IRON_PICKAXE),
                    player.getPotionEffect(OriginsReborn.getNMSInvoker().getMiningFatigueEffect()),
                    player.getPotionEffect(OriginsReborn.getNMSInvoker().getHasteEffect()),
                    player.getPotionEffect(PotionEffectType.CONDUIT_POWER),
                    OriginsReborn.getNMSInvoker().isUnderWater(player),
                    aquaAffinity,
                    player.isOnGround()
            );
        }

        @Override
        public boolean shouldActivate(Player player) {
            Block target = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
            return !MaterialTags.PICKAXES.isTagged(player.getInventory().getItemInMainHand().getType()) && target != null && naturalStones.contains(target.getType());
        }
    }
}
