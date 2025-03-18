package com.starshootercity.abilities;

import org.bukkit.event.Listener;

/**
 * @deprecated Ability interfaces have been moved to com.starshootercity.abilities.types
 * @see com.starshootercity.abilities.types.SkinChangingAbility
 */
@Deprecated(forRemoval = true)
public interface SkinChangingAbility extends com.starshootercity.abilities.types.SkinChangingAbility, Listener, Ability {
}
