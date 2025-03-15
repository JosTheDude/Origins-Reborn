package com.starshootercity.skript;

import com.starshootercity.abilities.VisibleAbility;
import net.kyori.adventure.key.Key;

public class NamedSkriptAbility extends SkriptAbility implements VisibleAbility {

    private final String title;
    private final String description;

    public NamedSkriptAbility(Key key, String title, String description) {
        super(key);
        this.title = title;
        this.description = description;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String title() {
        return title;
    }
}
