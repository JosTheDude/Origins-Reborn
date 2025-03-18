package com.starshootercity.skript;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.MultiAbility;
import net.kyori.adventure.key.Key;

import java.util.List;

public class ComplexSkriptAbility extends BasicSkriptAbility implements MultiAbility {

    private final List<Ability> abilities;

    public ComplexSkriptAbility(Key key, List<Ability> abilities) {
        super(key);
        this.abilities = abilities;
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }
}
