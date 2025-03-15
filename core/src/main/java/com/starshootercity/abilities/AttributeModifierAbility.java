package com.starshootercity.abilities;

import com.starshootercity.util.config.ConfigManager;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface AttributeModifierAbility extends Ability {

    String OPERATION = "attribute-modifier.operation";
    String EXP = "attribute-modifier.expression";

    @NotNull Attribute getAttribute();
    double getAmount();

    @SuppressWarnings("unused")
    default double getChangedAmount(Player player) {
        return 0;
    }

    @NotNull AttributeModifier.Operation getOperation();

    default double getTotalAmount(Player player) {
        double total = getAmount() + getChangedAmount(player);
        if (total != 0) {
            String modifiedValue = getConfigOption(EXP, ConfigManager.SettingType.STRING);
            try {
                return new ExpressionBuilder(modifiedValue).build().setVariable("x", total).evaluate();
            } catch (IllegalArgumentException ignored) {}
        }
        return total;
    }

    default AttributeModifier.@NotNull Operation getActualOperation() {
        String op = getConfigOption(OPERATION, ConfigManager.SettingType.STRING).toLowerCase();
        return switch (op) {
            case "add_scalar" -> AttributeModifier.Operation.ADD_SCALAR;
            case "add_number" -> AttributeModifier.Operation.ADD_NUMBER;
            case "multiply_scalar_1" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            default -> getOperation();
        };
    }

    default void setupAttributeConfig() {
        registerConfigOption(OPERATION, Collections.singletonList("The operation to use ('ADD_SCALAR', 'ADD_NUMBER' or 'MULTIPLY_SCALAR_1')"), ConfigManager.SettingType.STRING, getOperation().toString());
        registerConfigOption(EXP, List.of("A mathematical expression used to modify the attribute, where 'x' is a variable representing the default modifier.", "Example: 'x * 3' will triple the value used in the modifier."), ConfigManager.SettingType.STRING, "x");
    }
}
