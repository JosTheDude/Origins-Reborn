package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class Fragile implements AttributeModifierAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:fragile");
    }

    @Override
    public String description() {
        return "You have 3 less hearts of health than humans.";
    }

    @Override
    public String title() {
        return "Fragile";
    }

    @Override
    public @NotNull Attribute getAttribute() {
        return OriginsReborn.getNMSInvoker().getMaxHealthAttribute();
    }

    @Override
    public double getAmount() {
        return -6;
    }

    @Override
    public AttributeModifier.@NotNull Operation getOperation() {
        return AttributeModifier.Operation.ADD_NUMBER;
    }
}
