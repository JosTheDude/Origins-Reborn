package com.starshootercity.abilities;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.AttributeModifierAbility;
import com.starshootercity.abilities.types.MultiAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtraReach implements MultiAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:extra_reach");
    }

    @Override
    public String description() {
        return "You can reach blocks and entities further away.";
    }

    @Override
    public String title() {
        return "Slender Body";
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of(ExtraReachBlocks.extraReachBlocks, ExtraReachEntities.extraReachEntities);
    }

    private static Attribute blockRange;
    private static Attribute entityRange;

    public ExtraReach(Attribute blockRange, Attribute entityRange) {
        ExtraReach.blockRange = blockRange;
        ExtraReach.entityRange = entityRange;
    }

    public static class ExtraReachEntities implements AttributeModifierAbility {
        public static ExtraReachEntities extraReachEntities = new ExtraReachEntities();

        @Override
        public @NotNull Attribute getAttribute() {
            return entityRange;
        }

        @Override
        public double getAmount(Player player) {
            return 1.5;
        }

        @Override
        public AttributeModifier.@NotNull Operation getOperation() {
            return AttributeModifier.Operation.ADD_NUMBER;
        }

        @Override
        public @NotNull Key getKey() {
            return Key.key("origins:extra_reach_entities");
        }
    }

    public static class ExtraReachBlocks implements AttributeModifierAbility {
        public static ExtraReachBlocks extraReachBlocks = new ExtraReachBlocks();

        @Override
        public @NotNull Attribute getAttribute() {
            return blockRange;
        }

        @Override
        public double getAmount(Player player) {
            return 1.5;
        }

        @Override
        public AttributeModifier.@NotNull Operation getOperation() {
            return AttributeModifier.Operation.ADD_NUMBER;
        }

        @Override
        public @NotNull Key getKey() {
            return Key.key("origins:extra_reach_blocks");
        }
    }
}
