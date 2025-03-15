package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class Tailwind implements AttributeModifierAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:tailwind");
    }

    @Override
    public String description() {
        return "You are a little bit quicker on foot than others.";
    }

    @Override
    public String title() {
        return "Tailwind";
    }

    @Override
    public @NotNull Attribute getAttribute() {
        return OriginsReborn.getNMSInvoker().getMovementSpeedAttribute();
    }

    @Override
    public double getAmount() {
        return 0.2;
    }

    @Override
    public AttributeModifier.@NotNull Operation getOperation() {
        return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
    }
}
