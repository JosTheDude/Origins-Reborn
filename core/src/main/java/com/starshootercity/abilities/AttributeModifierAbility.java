package com.starshootercity.abilities;

import org.bukkit.entity.Player;

/**
 * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
 * @see com.starshootercity.abilities.types.AttributeModifierAbility
 */
@Deprecated(forRemoval = true)
public interface AttributeModifierAbility extends com.starshootercity.abilities.types.AttributeModifierAbility, Ability {

    @Override
    default double getAmount(Player player) {
        return getAmount() + getChangedAmount(player);
    }

    double getAmount();

    @SuppressWarnings("unused")
    default double getChangedAmount(Player player) {
        return 0;
    }
}
