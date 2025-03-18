package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.BreakSpeedModifierAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class AquaAffinity implements BreakSpeedModifierAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:aqua_affinity");
    }

    @Override
    public String description() {
        return "You may break blocks underwater as others do on land.";
    }

    @Override
    public String title() {
        return "Aqua Affinity";
    }

    @Override
    public BlockMiningContext getMiningContext(Player player) {
        return new BlockMiningContext(
                player.getInventory().getItemInMainHand(),
                player.getPotionEffect(OriginsReborn.getNMSInvoker().getMiningFatigueEffect()),
                player.getPotionEffect(OriginsReborn.getNMSInvoker().getHasteEffect()),
                player.getPotionEffect(PotionEffectType.CONDUIT_POWER),
                true,
                true,
                true
        );
    }

    @Override
    public boolean shouldActivate(Player player) {
        return player.isInWater();
    }
}
