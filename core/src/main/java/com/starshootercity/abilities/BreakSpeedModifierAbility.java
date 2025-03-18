package com.starshootercity.abilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
 * @see com.starshootercity.abilities.types.DefaultSpawnAbility
 */
@Deprecated(forRemoval = true)
public interface BreakSpeedModifierAbility extends com.starshootercity.abilities.types.BreakSpeedModifierAbility, Ability {

    default com.starshootercity.abilities.types.BreakSpeedModifierAbility.BlockMiningContext getMiningContext(Player player) {
        BlockMiningContext ctx = provideContextFor(player);
        return new com.starshootercity.abilities.types.BreakSpeedModifierAbility.BlockMiningContext(
                ctx.heldItem,
                ctx.slowDigging,
                ctx.fastDigging,
                ctx.conduitPower,
                ctx.underwater,
                ctx.aquaAffinity,
                ctx.onGround
        );
    }

    BlockMiningContext provideContextFor(Player player);

    record BlockMiningContext(ItemStack heldItem, @Nullable PotionEffect slowDigging, @Nullable PotionEffect fastDigging, @Nullable PotionEffect conduitPower, boolean underwater, boolean aquaAffinity, boolean onGround) {
    }
}
