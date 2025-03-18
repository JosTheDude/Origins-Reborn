package com.starshootercity.cooldowns;

import com.starshootercity.abilities.Ability;

/**
 * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
 * @see com.starshootercity.abilities.types.CooldownAbility
 */
@Deprecated(forRemoval = true)
public interface CooldownAbility extends com.starshootercity.abilities.types.CooldownAbility, Ability {
}
