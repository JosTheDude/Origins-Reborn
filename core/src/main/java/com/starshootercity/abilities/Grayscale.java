package com.starshootercity.abilities;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class Grayscale implements SkinChangingAbility, VisibleAbility {

    @Override
    public void modifySkin(BufferedImage image, Player player) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, grayscale(image.getRGB(x, y)));
            }
        }
    }

    public int grayscale(int decimalRGB) {
        int a = decimalRGB >> 24 & 0xFF;
        int r = (decimalRGB >> 16) & 0xFF;
        int g = (decimalRGB >> 8) & 0xFF;
        int b = decimalRGB & 0xFF;

        int grayscale = (int) Math.min(255, Math.max(0, 0.299 * r + 0.587 * g + 0.114 * b));

        return a << 24 | (grayscale << 16) | (grayscale << 8) | grayscale;
    }

    @Override
    public String description() {
        return "You appear faded, the color drained from your body.";
    }

    @Override
    public String title() {
        return "Faded";
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:grayscale");
    }
}
