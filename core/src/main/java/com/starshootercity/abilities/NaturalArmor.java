package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class NaturalArmor implements AttributeModifierAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:natural_armor");
    }

    @Override
    public @NotNull Attribute getAttribute() {
        return OriginsReborn.getNMSInvoker().getArmorAttribute();
    }

    @Override
    public double getAmount() {
        return 8;
    }

    @Override
    public AttributeModifier.@NotNull Operation getOperation() {
        return AttributeModifier.Operation.ADD_NUMBER;
    }

    @Override
    public String description() {
        return "Even without wearing armor, your skin provides natural protection.";
    }

    @Override
    public String title() {
        return "Sturdy Skin";
    }
}
