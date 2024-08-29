package com.starshootercity;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.abilities.*;
import com.starshootercity.commands.OriginCommand;
import com.starshootercity.events.PlayerSwapOriginEvent;
import com.starshootercity.geysermc.GeyserSwapper;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OriginSwapper implements Listener {
    private final static NamespacedKey displayKey = new NamespacedKey(OriginsReborn.getInstance(), "display-item");
    private final static NamespacedKey confirmKey = new NamespacedKey(OriginsReborn.getInstance(), "confirm-select");
    private final static NamespacedKey costsCurrencyKey = new NamespacedKey(OriginsReborn.getInstance(), "costs-currency");
    private final static NamespacedKey originKey = new NamespacedKey(OriginsReborn.getInstance(), "origin-name");
    private final static NamespacedKey swapTypeKey = new NamespacedKey(OriginsReborn.getInstance(), "swap-type");
    private final static NamespacedKey pageSetKey = new NamespacedKey(OriginsReborn.getInstance(), "page-set");
    private final static NamespacedKey pageScrollKey = new NamespacedKey(OriginsReborn.getInstance(), "page-scroll");
    private final static NamespacedKey costKey = new NamespacedKey(OriginsReborn.getInstance(), "enable-cost");
    private final static NamespacedKey displayOnlyKey = new NamespacedKey(OriginsReborn.getInstance(), "display-only");
    private final static NamespacedKey closeKey = new NamespacedKey(OriginsReborn.getInstance(), "close");
    private final static Random random = new Random();

    public static String getInverse(String string) {
        StringBuilder result = new StringBuilder();
        for (char c : string.toCharArray()) {
            result.append(getInverse(c));
        }
        return result.toString();
    }

    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount) {
        openOriginSwapper(player, reason, slot, scrollAmount, false, false);
    }
    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount, boolean cost) {
        openOriginSwapper(player, reason, slot, scrollAmount, cost, false);
    }
    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount, boolean cost, boolean displayOnly) {
        if (shouldDisallowSelection(player, reason)) return;
        if (reason == PlayerSwapOriginEvent.SwapReason.INITIAL) {
            String def = OriginsReborn.getInstance().getConfig().getString("origin-selection.default_origin", "NONE");
            Origin defaultOrigin = AddonLoader.originFileNameMap.get(def);
            if (defaultOrigin != null) {
                setOrigin(player, defaultOrigin, reason, false);
                return;
            }
        }
        lastSwapReasons.put(player, reason);
        boolean enableRandom = OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.random-option.enabled");
        if (GeyserSwapper.checkBedrockSwap(player, reason, cost, displayOnly)) {
            if (AddonLoader.origins.isEmpty()) return;
            List<Origin> origins = new ArrayList<>(AddonLoader.origins);
            if (!displayOnly) origins.removeIf(origin -> origin.isUnchoosable(player) || origin.hasPermission() && !player.hasPermission(origin.getPermission()));
            while (slot > origins.size() || slot == origins.size() && !enableRandom) {
                slot -= origins.size() + (enableRandom ? 1 : 0);
            }
            while (slot < 0) {
                slot += origins.size() + (enableRandom ? 1 : 0);
            }
            ItemStack icon;
            String name;
            char impact;
            int amount = OriginsReborn.getInstance().getConfig().getInt("swap-command.vault.default-cost", 1000);
            LineData data;
            if (slot == origins.size()) {
                List<String> excludedOrigins = OriginsReborn.getInstance().getConfig().getStringList("origin-selection.random-option.exclude");
                List<String> excludedOriginNames = new ArrayList<>();
                for (String s : excludedOrigins) {
                    Origin origin = AddonLoader.originFileNameMap.get(s);
                    if (origin == null) continue;
                    excludedOriginNames.add(AddonLoader.getTextFor("origin." + origin.getAddon().getNamespace() + "." + s.replace(" ", "_").toLowerCase() + ".name", origin.getName()));
                }
                icon = OrbOfOrigin.orb;
                name = AddonLoader.getTextFor("origin.origins.random.name", "Random");
                impact = '\uE002';
                StringBuilder names = new StringBuilder("%s\n\n".formatted(AddonLoader.getTextFor("origin.origins.random.description", "You'll be assigned one of the following:")));
                for (Origin origin : origins) {
                    if (!excludedOriginNames.contains(origin.getName())) {
                        names.append(origin.getName()).append("\n");
                    }
                }
                data = new LineData(LineData.makeLineFor(
                        names.toString(),
                        LineData.LineComponent.LineType.DESCRIPTION
                ));
            } else {
                Origin origin = origins.get(slot);
                icon = origin.getIcon();
                name = origin.getName();
                impact = origin.getImpact();
                data = new LineData(origin);
                if (origin.getCost() != null) {
                    amount = origin.getCost();
                }
            }
            StringBuilder compressedName = new StringBuilder("\uF001");
            for (char c : name.toCharArray()) {
                compressedName.append(c);
                compressedName.append('\uF000');
            }
            Component background = applyFont(ShortcutUtils.getColored(OriginsReborn.getInstance().getConfig().getString("origin-selection.screen-title.background", "")), Key.key("minecraft:default"));
            Component component = applyFont(Component.text("\uF000\uE000\uF001\uE001\uF002" + impact),
                    Key.key("minecraft:origin_selector"))
                    .color(NamedTextColor.WHITE)
                    .append(background)
                    .append(applyFont(Component.text(compressedName.toString()),
                            Key.key("minecraft:origin_title_text")
                    ).color(NamedTextColor.WHITE))
                    .append(applyFont(Component.text(getInverse(name) + "\uF000"),
                            Key.key("minecraft:reverse_text")
                    ).color(NamedTextColor.WHITE));
            for (Component c : data.getLines(scrollAmount)) {
                component = component.append(c);
            }
            Component prefix = applyFont(ShortcutUtils.getColored(OriginsReborn.getInstance().getConfig().getString("origin-selection.screen-title.prefix", "")), Key.key("minecraft:default"));
            Component suffix = applyFont(ShortcutUtils.getColored(OriginsReborn.getInstance().getConfig().getString("origin-selection.screen-title.suffix", "")), Key.key("minecraft:default"));
            Inventory swapperInventory = Bukkit.createInventory(null, 54,
                    prefix.append(component).append(suffix)
            );
            ItemMeta meta = icon.getItemMeta();
            meta.getPersistentDataContainer().set(originKey, PersistentDataType.STRING, name.toLowerCase());
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
            }
            meta.getPersistentDataContainer().set(displayKey, BooleanPDT.BOOLEAN, true);
            meta.getPersistentDataContainer().set(swapTypeKey, PersistentDataType.STRING, reason.getReason());
            icon.setItemMeta(meta);
            swapperInventory.setItem(1, icon);
            ItemStack confirm = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemStack invisibleConfirm = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta confirmMeta = confirm.getItemMeta();
            ItemMeta invisibleConfirmMeta = invisibleConfirm.getItemMeta();

            confirmMeta.displayName(Component.text("Confirm")
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            confirmMeta.setCustomModelData(5);
            if (!displayOnly) confirmMeta.getPersistentDataContainer().set(confirmKey, BooleanPDT.BOOLEAN, true);
            else confirmMeta.getPersistentDataContainer().set(closeKey, BooleanPDT.BOOLEAN, true);

            invisibleConfirmMeta.displayName(Component.text("Confirm")
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            invisibleConfirmMeta.setCustomModelData(6);
            if (!displayOnly) invisibleConfirmMeta.getPersistentDataContainer().set(confirmKey, BooleanPDT.BOOLEAN, true);
            else invisibleConfirmMeta.getPersistentDataContainer().set(closeKey, BooleanPDT.BOOLEAN, true);

            if (amount != 0 && cost && !player.hasPermission(OriginsReborn.getInstance().getConfig().getString("swap-command.vault.bypass-permission", "originsreborn.costbypass"))) {
                boolean go = true;
                if (OriginsReborn.getInstance().getConfig().getBoolean("swap-command.vault.permanent-purchases")) {
                    go = !getUsedOriginFileConfiguration().getStringList(player.getUniqueId().toString()).contains(name);
                }
                if (go) {
                    String symbol = OriginsReborn.getInstance().getConfig().getString("swap-command.vault.currency-symbol", "$");
                    List<Component> costsCurrency = List.of(
                            Component.text((OriginsReborn.getInstance().getEconomy().has(player, amount) ? "This will cost %s%s of your balance!" : "You need at least %s%s in your balance to do this!").formatted(symbol, amount))
                    );
                    confirmMeta.lore(costsCurrency);
                    invisibleConfirmMeta.lore(costsCurrency);
                    confirmMeta.getPersistentDataContainer().set(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                    invisibleConfirmMeta.getPersistentDataContainer().set(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                }
            }

            ItemStack up = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemStack down = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta upMeta = up.getItemMeta();
            ItemMeta downMeta = down.getItemMeta();

            int scrollSize = OriginsReborn.getInstance().getConfig().getInt("origin-selection.scroll-amount", 1);

            upMeta.displayName(Component.text("Up")
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            if (scrollAmount != 0) {
                upMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot);
                upMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, Math.max(scrollAmount - scrollSize, 0));
            }
            upMeta.setCustomModelData(3 + (scrollAmount == 0 ? 6 : 0));
            upMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
            upMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, displayOnly);


            int size = data.lines.size() - scrollAmount - 6;
            boolean canGoDown = size > 0;

            downMeta.displayName(Component.text("Down")
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            if (canGoDown) {
                downMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot);
                downMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, Math.min(scrollAmount + scrollSize, scrollAmount + size));
            }
            downMeta.setCustomModelData(4 + (!canGoDown ? 6 : 0));
            downMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
            downMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, displayOnly);


            up.setItemMeta(upMeta);
            down.setItemMeta(downMeta);
            swapperInventory.setItem(52, up);
            swapperInventory.setItem(53, down);


            // Change to unchoosable
            if (!displayOnly) {
                ItemStack left = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                ItemStack right = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                ItemMeta leftMeta = left.getItemMeta();
                ItemMeta rightMeta = right.getItemMeta();

                leftMeta.displayName(Component.text("Previous origin")
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
                leftMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot - 1);
                leftMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, 0);
                leftMeta.setCustomModelData(1);
                leftMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
                leftMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, false);

                rightMeta.displayName(Component.text("Next origin")
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
                rightMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot + 1);
                rightMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, 0);
                rightMeta.setCustomModelData(2);
                rightMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
                rightMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, false);


                left.setItemMeta(leftMeta);
                right.setItemMeta(rightMeta);

                swapperInventory.setItem(47, left);
                swapperInventory.setItem(51, right);
            }

            confirm.setItemMeta(confirmMeta);
            invisibleConfirm.setItemMeta(invisibleConfirmMeta);
            swapperInventory.setItem(48, confirm);
            swapperInventory.setItem(49, invisibleConfirm);
            swapperInventory.setItem(50, invisibleConfirm);
            player.openInventory(swapperInventory);
        }
    }

    public static Component applyFont(Component component, Key font) {
        return OriginsReborn.getNMSInvoker().applyFont(component, font);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getWhoClicked().getOpenInventory().getItem(1);
        if (item != null) {
            if (item.getItemMeta() == null) return;
            if (item.getItemMeta().getPersistentDataContainer().has(displayKey, BooleanPDT.BOOLEAN)) {
                event.setCancelled(true);
            }
            if (event.getWhoClicked() instanceof Player player) {
                ItemStack currentItem = event.getCurrentItem();
                if (currentItem == null || currentItem.getItemMeta() == null) return;
                Integer page = currentItem.getItemMeta().getPersistentDataContainer().get(pageSetKey, PersistentDataType.INTEGER);
                if (page != null) {
                    boolean cost = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(costKey, BooleanPDT.BOOLEAN, false);
                    boolean allowUnchoosable = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(displayOnlyKey, BooleanPDT.BOOLEAN, false);
                    Integer scroll = currentItem.getItemMeta().getPersistentDataContainer().get(pageScrollKey, PersistentDataType.INTEGER);
                    if (scroll == null) return;
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    openOriginSwapper(player, getReason(item), page, scroll, cost, allowUnchoosable);
                }
                if (currentItem.getItemMeta().getPersistentDataContainer().has(confirmKey, BooleanPDT.BOOLEAN)) {
                    int amount = OriginsReborn.getInstance().getConfig().getInt("swap-command.vault.cost", 1000);
                    if (!player.hasPermission(OriginsReborn.getInstance().getConfig().getString("swap-command.vault.bypass-permission", "originsreborn.costbypass")) && currentItem.getItemMeta().getPersistentDataContainer().has(costsCurrencyKey, PersistentDataType.INTEGER)) {
                        amount = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                        if (!OriginsReborn.getInstance().getEconomy().has(player, amount)) {
                            return;
                        } else {
                            OriginsReborn.getInstance().getEconomy().withdrawPlayer(player, amount);
                        }
                    }
                    String originName = item.getItemMeta().getPersistentDataContainer().get(originKey, PersistentDataType.STRING);
                    if (originName == null) return;
                    Origin origin;
                    if (originName.equalsIgnoreCase("random")) {
                        List<String> excludedOrigins = OriginsReborn.getInstance().getConfig().getStringList("origin-selection.random-option.exclude");
                        List<Origin> origins = new ArrayList<>(AddonLoader.origins);
                        origins.removeIf(origin1 -> excludedOrigins.contains(origin1.getName()));
                        origins.removeIf(origin1 -> origin1.isUnchoosable(player));
                        if (origins.isEmpty()) {
                            origin = AddonLoader.origins.get(0);
                        } else {
                            origin = origins.get(random.nextInt(origins.size()));
                        }
                    } else {
                        origin = AddonLoader.originNameMap.get(originName);
                    }
                    PlayerSwapOriginEvent.SwapReason reason = getReason(item);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    player.closeInventory();

                    ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();

                    if (reason == PlayerSwapOriginEvent.SwapReason.ORB_OF_ORIGIN) {
                        EquipmentSlot hand = null;
                        if (meta != null) {
                            if (meta.getPersistentDataContainer().has(OrbOfOrigin.orbKey, BooleanPDT.BOOLEAN)) {
                                hand = EquipmentSlot.HAND;
                            }
                        }
                        if (hand == null) {
                            ItemMeta offhandMeta = player.getInventory().getItemInOffHand().getItemMeta();
                            if (offhandMeta != null) {
                                if (offhandMeta.getPersistentDataContainer().has(OrbOfOrigin.orbKey, BooleanPDT.BOOLEAN)) {
                                    hand = EquipmentSlot.OFF_HAND;
                                }
                            }
                        }
                        if (hand == null) return;
                        orbCooldown.put(player, System.currentTimeMillis());
                        if (hand == EquipmentSlot.HAND) player.swingMainHand();
                        else player.swingOffHand();
                        if (OriginsReborn.getInstance().getConfig().getBoolean("orb-of-origin.consume")) {
                            ItemStack handItem = player.getInventory().getItem(hand);
                            if (handItem != null) handItem.setAmount(handItem.getAmount() - 1);
                        }
                    }
                    boolean resetPlayer = shouldResetPlayer(reason);
                    if (origin.isUnchoosable(player)) {
                        openOriginSwapper(player, reason, 0, 0);
                        return;
                    }
                    OriginsReborn.getCooldowns().setCooldown(player, OriginCommand.key);
                    setOrigin(player, origin, reason, resetPlayer);
                } else if (currentItem.getItemMeta().getPersistentDataContainer().has(closeKey, BooleanPDT.BOOLEAN)) event.getWhoClicked().closeInventory();
            }
        }
    }

    public static boolean shouldResetPlayer(PlayerSwapOriginEvent.SwapReason reason) {
        return switch (reason) {
            case COMMAND -> OriginsReborn.getInstance().getConfig().getBoolean("swap-command.reset-player");
            case ORB_OF_ORIGIN -> OriginsReborn.getInstance().getConfig().getBoolean("orb-of-origin.reset-player");
            default -> false;
        };
    }

    public static int getWidth(char c) {
        return switch (c) {
            case ' ', '"', '(', ')', '*', 'I', '[', ']', 't', '{', '}' -> 4;
            case '!', '\'', ',', '.', ':', ';', 'i', '|', '\uf00a' -> 2;
            case '#', '$', '%', '&', '+', '-', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '=', '?', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '\\', '^', '_', 'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'm', 'n', 'o', 'p', 'q', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z' ->
                    6;
            case '<', '>', 'f', 'k' -> 5;
            case '@', '~' -> 7;
            case '`', 'l' -> 3;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    public static int getWidth(String s) {
        int result = 0;
        for (char c : s.toCharArray()) {
            result += getWidth(c);
        }
        return result;
    }

    public static String getInverse(char c) {
        return switch (getWidth(c)) {
            case 2 -> "\uF001";
            case 3 -> "\uF002";
            case 4 -> "\uF003";
            case 5 -> "\uF004";
            case 6 -> "\uF005";
            case 7 -> "\uF006";
            case 8 -> "\uF007";
            case 9 -> "\uF008";
            case 10 -> "\uF009";
            case 11 -> "\uF008\uF001";
            case 12 -> "\uF009\uF001";
            case 13 -> "\uF009\uF002";
            case 14 -> "\uF009\uF003";
            case 15 -> "\uF009\uF004";
            case 16 -> "\uF009\uF005";
            case 17 -> "\uF009\uF006";
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    public static Map<Player, Long> orbCooldown = new HashMap<>();

    public static void resetPlayer(Player player, boolean full) {
        resetAttributes(player);
        player.closeInventory();
        OriginsReborn.getNMSInvoker().setWorldBorderOverlay(player, false);
        player.setCooldown(Material.SHIELD, 0);
        player.setAllowFlight(false);
        player.setFlying(false);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            AbilityRegister.updateEntity(player, otherPlayer);
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getAmplifier() == -1 || ShortcutUtils.isInfinite(effect)) player.removePotionEffect(effect.getType());
        }
        if (!full) return;
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setSaturation(5);
        player.setFallDistance(0);
        player.setRemainingAir(player.getMaximumAir());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setHealth(getMaxHealth(player));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        World world = getRespawnWorld(getOrigin(player));
        player.teleport(world.getSpawnLocation());
        OriginsReborn.getNMSInvoker().resetRespawnLocation(player);
    }

    public static @NotNull World getRespawnWorld(@Nullable Origin origin) {
        if (origin != null) {
            for (Ability ability : origin.getAbilities()) {
                if (ability instanceof DefaultSpawnAbility defaultSpawnAbility) {
                    World world = defaultSpawnAbility.getWorld();
                    if (world != null) return world;
                }
            }
        }
        String overworld = OriginsReborn.getInstance().getConfig().getString("worlds.world");
        if (overworld == null) {
            overworld = "world";
            OriginsReborn.getInstance().getConfig().set("worlds.world", "world");
            OriginsReborn.getInstance().saveConfig();
        }
        World world = Bukkit.getWorld(overworld);
        if (world == null) return Bukkit.getWorlds().get(0);
        return world;
    }

    public static double getMaxHealth(Player player) {
        applyAttributeChanges(player);
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance == null) return 20;
        return instance.getValue();
    }

    public static void applyAttributeChanges(Player player) {
        for (Ability ability : AbilityRegister.abilityMap.values()) {
            if (ability instanceof AttributeModifierAbility attributeModifierAbility) {
                AttributeInstance instance = player.getAttribute(attributeModifierAbility.getAttribute());
                if (instance == null) continue;
                NamespacedKey key = new NamespacedKey(OriginsReborn.getInstance(), ability.getKey().asString().replace(":", "-"));
                if (AbilityRegister.hasAbility(player, ability.getKey())) {
                    AttributeModifier modifier = OriginsReborn.getNMSInvoker().getAttributeModifier(instance, key);
                    if (modifier != null) {
                        if (modifier.getAmount() == attributeModifierAbility.getTotalAmount(player)) {
                            continue;
                        } else instance.removeModifier(modifier);
                    }
                    OriginsReborn.getNMSInvoker().addAttributeModifier(instance, key, attributeModifierAbility.getKey().asString(), attributeModifierAbility.getTotalAmount(player), attributeModifierAbility.getActualOperation());
                } else {
                    AttributeModifier am = OriginsReborn.getNMSInvoker().getAttributeModifier(instance, key);
                    if (am != null) instance.removeModifier(am);
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerJoin(PlayerJoinEvent event) {
        resetAttributes(event.getPlayer());
        Origin origin = getOrigin(event.getPlayer());
        if (origin != null) {
            if (origin.getTeam() == null) return;
            origin.getTeam().addPlayer(event.getPlayer());
        } else {
            if (AddonLoader.getDefaultOrigin() != null) {
                setOrigin(event.getPlayer(), AddonLoader.getDefaultOrigin(), PlayerSwapOriginEvent.SwapReason.INITIAL, false);
            } else if (OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise")) {
                selectRandomOrigin(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL);
            } else if (ShortcutUtils.isBedrockPlayer(event.getPlayer().getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> GeyserSwapper.openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, false, false), OriginsReborn.getInstance().getConfig().getInt("geyser.join-form-delay", 20));
            } else {
                openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, 0, 0);
            }
        }
    }

    public static void resetAttributes(Player player) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;
            for (AttributeModifier modifier : instance.getModifiers()) {
                instance.removeModifier(modifier);
            }
        }
    }

    private static final Map<Player, PlayerSwapOriginEvent.SwapReason> lastSwapReasons = new HashMap<>();

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (shouldDisallowSelection(player, lastSwapReasons.getOrDefault(player, PlayerSwapOriginEvent.SwapReason.INITIAL))) continue;
            if (!OriginsReborn.getInstance().getConfig().getBoolean("misc-settings.disable-flight-stuff")) {
                player.setAllowFlight(AbilityRegister.canFly(player));
                AbilityRegister.updateFlight(player);
            }
            player.setInvisible(AbilityRegister.isInvisible(player));
            applyAttributeChanges(player);
            if (getOrigin(player) == null && player.getOpenInventory().getType() != InventoryType.CHEST) {
                if (AddonLoader.getDefaultOrigin() != null) {
                    setOrigin(player, AddonLoader.getDefaultOrigin(), PlayerSwapOriginEvent.SwapReason.INITIAL, false);
                }
                if (!OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise") && !ShortcutUtils.isBedrockPlayer(player.getUniqueId())) {
                    openOriginSwapper(player, lastSwapReasons.getOrDefault(player, PlayerSwapOriginEvent.SwapReason.INITIAL), 0, 0);
                }
            }
        }
    }

    public static boolean shouldDisallowSelection(Player player, PlayerSwapOriginEvent.SwapReason reason) {
        try {
            if (!AuthMeApi.getInstance().isAuthenticated(player)) return true;
        } catch (NoClassDefFoundError ignored) {}
        String worldId = player.getWorld().getName();
        return !AddonLoader.shouldOpenSwapMenu(player, reason) || OriginsReborn.getInstance().getConfig().getStringList("worlds.disabled-worlds").contains(worldId);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (invulnerableMode.equalsIgnoreCase("INITIAL") && getOrigin(player) == null) event.setCancelled(true);
            else if (invulnerableMode.equalsIgnoreCase("ON")) {
                ItemStack item = player.getOpenInventory().getTopInventory().getItem(1);
                if (item != null && item.getItemMeta() != null) {
                    if (item.getItemMeta().getPersistentDataContainer().has(originKey, PersistentDataType.STRING)) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwapOrigin(PlayerSwapOriginEvent event) {
        if (event.getNewOrigin() == null) return;

        String name = "default";
        if (OriginsReborn.getInstance().getConfig().contains("commands-on-origin.%s".formatted(name))) {
            for (String s : OriginsReborn.getInstance().getConfig().getStringList("commands-on-origin.%s".formatted(name))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", event.getPlayer().getName()).replace("%uuid%", event.getPlayer().getUniqueId().toString()));
            }
        }

        name = event.getNewOrigin().getActualName().replace(" ", "_").toLowerCase();
        if (OriginsReborn.getInstance().getConfig().contains("commands-on-origin.%s".formatted(name))) {
            for (String s : OriginsReborn.getInstance().getConfig().getStringList("commands-on-origin.%s".formatted(name))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", event.getPlayer().getName()).replace("%uuid%", event.getPlayer().getUniqueId().toString()));
            }
        }

        if (!OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.auto-spawn-teleport")) return;
        if (event.getReason() == PlayerSwapOriginEvent.SwapReason.INITIAL || event.getReason() == PlayerSwapOriginEvent.SwapReason.DIED) {
            Location loc = OriginsReborn.getNMSInvoker().getRespawnLocation(event.getPlayer());
            event.getPlayer().teleport(Objects.requireNonNullElseGet(loc, () -> getRespawnWorld(event.getNewOrigin()).getSpawnLocation()));
        }
    }

    public void selectRandomOrigin(Player player, PlayerSwapOriginEvent.SwapReason reason) {
        Origin origin = AddonLoader.origins.get(random.nextInt(AddonLoader.origins.size()));
        setOrigin(player, origin, reason, shouldResetPlayer(reason));
        openOriginSwapper(player, reason, AddonLoader.origins.indexOf(origin), 0, false, true);
    }

    private final Map<Player, Set<PlayerRespawnEvent.RespawnFlag>> lastRespawnReasons = new HashMap<>();

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (OriginsReborn.getNMSInvoker().getRespawnLocation(event.getPlayer()) == null) {
            World world = getRespawnWorld(getOrigin(event.getPlayer()));
            event.setRespawnLocation(world.getSpawnLocation());
        }

        lastRespawnReasons.put(event.getPlayer(), event.getRespawnFlags());
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        if (lastRespawnReasons.get(event.getPlayer()).contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) return;
        FileConfiguration config = OriginsReborn.getInstance().getConfig();
        if (config.getBoolean("origin-selection.death-origin-change")) {
            setOrigin(event.getPlayer(), null, PlayerSwapOriginEvent.SwapReason.DIED, false);
            if (OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise")) {
                selectRandomOrigin(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL);
            } else openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, 0, 0);
        }
    }

    public PlayerSwapOriginEvent.SwapReason getReason(ItemStack icon) {
        return PlayerSwapOriginEvent.SwapReason.get(icon.getItemMeta().getPersistentDataContainer().get(swapTypeKey, PersistentDataType.STRING));
    }

    public static Origin getOrigin(Player player) {
        if (player.getPersistentDataContainer().has(originKey, PersistentDataType.STRING)) {
            return AddonLoader.originNameMap.get(player.getPersistentDataContainer().get(originKey, PersistentDataType.STRING));
        } else {
            String name = originFileConfiguration.getString(player.getUniqueId().toString(), "null");
            player.getPersistentDataContainer().set(originKey, PersistentDataType.STRING, name);
            return AddonLoader.originNameMap.get(name);
        }
    }

    public static FileConfiguration getUsedOriginFileConfiguration() {
        return usedOriginFileConfiguration;
    }

    @SuppressWarnings("deprecation")
    public static void setOrigin(Player player, @Nullable Origin origin, PlayerSwapOriginEvent.SwapReason reason, boolean resetPlayer) {
        PlayerSwapOriginEvent swapOriginEvent = new PlayerSwapOriginEvent(player, reason, resetPlayer, getOrigin(player), origin);
        if (!swapOriginEvent.callEvent()) return;
        if (swapOriginEvent.getNewOrigin() == null) {
            originFileConfiguration.set(player.getUniqueId().toString(), null);
            player.getPersistentDataContainer().remove(originKey);
            saveOrigins();
            resetPlayer(player, swapOriginEvent.isResetPlayer());
            return;
        }
        if (swapOriginEvent.getNewOrigin().getTeam() != null) {
            swapOriginEvent.getNewOrigin().getTeam().addPlayer(player);
        }
        OriginsReborn.getCooldowns().resetCooldowns(player);
        originFileConfiguration.set(player.getUniqueId().toString(), swapOriginEvent.getNewOrigin().getName().toLowerCase());
        player.getPersistentDataContainer().set(originKey, PersistentDataType.STRING, swapOriginEvent.getNewOrigin().getName().toLowerCase());
        saveOrigins();
        List<String> usedOrigins = new ArrayList<>(usedOriginFileConfiguration.getStringList(player.getUniqueId().toString()));
        usedOrigins.add(swapOriginEvent.getNewOrigin().getName().toLowerCase());
        usedOriginFileConfiguration.set(player.getUniqueId().toString(), usedOrigins);
        saveUsedOrigins();
        resetPlayer(player, swapOriginEvent.isResetPlayer());
    }

    private static File originFile;
    private static FileConfiguration originFileConfiguration;

    private static File usedOriginFile;
    private static FileConfiguration usedOriginFileConfiguration;

    public static FileConfiguration getOriginFileConfiguration() {
        return originFileConfiguration;
    }

    private final String invulnerableMode;

    public OriginSwapper() {
        invulnerableMode = OriginsReborn.getInstance().getConfig().getString("origin-selection.invulnerable-mode", "OFF");

        originFile = new File(OriginsReborn.getInstance().getDataFolder(), "selected-origins.yml");
        if (!originFile.exists()) {
            boolean ignored = originFile.getParentFile().mkdirs();
            OriginsReborn.getInstance().saveResource("selected-origins.yml", false);
        }
        originFileConfiguration = new YamlConfiguration();
        try {
            originFileConfiguration.load(originFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        usedOriginFile = new File(OriginsReborn.getInstance().getDataFolder(), "used-origins.yml");
        if (!usedOriginFile.exists()) {
            boolean ignored = usedOriginFile.getParentFile().mkdirs();
            OriginsReborn.getInstance().saveResource("used-origins.yml", false);
        }
        usedOriginFileConfiguration = new YamlConfiguration();
        try {
            usedOriginFileConfiguration.load(usedOriginFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveOrigins() {
        try {
            originFileConfiguration.save(originFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveUsedOrigins() {
        try {
            usedOriginFileConfiguration.save(usedOriginFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class LineData {
        public static List<LineComponent> makeLineFor(String text, LineComponent.LineType type) {
            StringBuilder result = new StringBuilder();
            StringBuilder rawResult = new StringBuilder();
            List<LineComponent> list = new ArrayList<>();
            List<String> splitLines = new ArrayList<>(Arrays.stream(text.split("\n", 2)).toList());
            StringBuilder otherPart = new StringBuilder();
            String firstLine = splitLines.remove(0);
            if (firstLine.contains(" ") && getWidth(firstLine) > 140) {
                List<String> split = new ArrayList<>(Arrays.stream(firstLine.split(" ")).toList());
                StringBuilder firstPart = new StringBuilder(split.get(0));
                split.remove(0);
                boolean canAdd = true;
                for (String s : split) {
                    if (canAdd && getWidth(firstPart + " " + s) <= 140) {
                        firstPart.append(" ");
                        firstPart.append(s);
                    } else {
                        canAdd = false;
                        if (!otherPart.isEmpty()) otherPart.append(" ");
                        otherPart.append(s);
                    }
                }
                firstLine = firstPart.toString();
            }
            for (String s : splitLines) {
                if (!otherPart.isEmpty()) otherPart.append("\n");
                otherPart.append(s);
            }
            if (type == LineComponent.LineType.DESCRIPTION) firstLine = '\uF00A' + firstLine;
            for (char c : firstLine.toCharArray()) {
                result.append(c);
                rawResult.append(c == '\uF00A' ? "" : c);
                result.append('\uF000');
            }
            rawResult.append(' ');
            String finalText = firstLine;
            list.add(new LineComponent(
                    Component.text(result.toString())
                            .color(type == LineComponent.LineType.TITLE ? NamedTextColor.WHITE : TextColor.fromHexString("#CACACA"))
                            .append(Component.text(getInverse(finalText))),
                    type,
                    rawResult.toString()
            ));
            if (!otherPart.isEmpty()) {
                list.addAll(makeLineFor(otherPart.toString(), type));
            }
            return list;
        }
        public static class LineComponent {
            public enum LineType {
                TITLE,
                DESCRIPTION
            }
            private final Component component;
            private final LineType type;
            private final String rawText;
            private final boolean empty;

            public boolean isEmpty() {
                return empty;
            }

            public LineType getType() {
                return type;
            }

            public LineComponent(Component component, LineType type, String rawText) {
                this.component = component;
                this.type = type;
                this.rawText = rawText;
                this.empty = false;
            }

            public LineComponent() {
                this.type = LineType.DESCRIPTION;
                this.component = Component.empty();
                this.rawText = "";
                this.empty = true;
            }

            public String getRawText() {
                return rawText;
            }

            public Component getComponent(int lineNumber) {
                @Subst("minecraft:text_line_0") String formatted = "minecraft:%stext_line_%s".formatted(type == LineType.DESCRIPTION ? "" : "title_", lineNumber);
                return applyFont(component, Key.key(formatted));
            }
        }
        private final List<LineComponent> lines;

        public LineData(Origin origin) {
            lines = new ArrayList<>();
            lines.addAll(makeLineFor(origin.getDescription(), LineComponent.LineType.DESCRIPTION));
            List<VisibleAbility> visibleAbilities = origin.getVisibleAbilities();
            int size = visibleAbilities.size();
            int count = 0;
            if (size > 0) lines.add(new LineComponent());
            for (VisibleAbility visibleAbility : visibleAbilities) {
                count++;
                lines.addAll(visibleAbility.getUsedTitle());
                lines.addAll(visibleAbility.getUsedDescription());
                if (count < size) lines.add(new LineComponent());
            }
        }
        public LineData(List<LineComponent> lines) {
            this.lines = lines;
        }

        public List<Component> getLines(int startingPoint) {
            List<Component> resultLines = new ArrayList<>();
            for (int i = startingPoint; i < startingPoint + 6 && i < lines.size(); i++) {
                resultLines.add(lines.get(i).getComponent(i - startingPoint));
            }
            return resultLines;
        }

        public List<LineComponent> getRawLines() {
            return lines;
        }
    }

    public static class BooleanPDT implements PersistentDataType<Byte, Boolean> {
        public static BooleanPDT BOOLEAN = new BooleanPDT();

        @Override
        public @NotNull Class<Byte> getPrimitiveType() {
            return Byte.class;
        }

        @Override
        public @NotNull Class<Boolean> getComplexType() {
            return Boolean.class;
        }

        @Override
        public @NotNull Byte toPrimitive(@NotNull Boolean aBoolean, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return (byte) (aBoolean ? 1 : 0);
        }

        @Override
        public @NotNull Boolean fromPrimitive(@NotNull Byte aByte, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return aByte >= 1;
        }
    }
}
