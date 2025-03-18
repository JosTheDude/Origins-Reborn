package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ScareCreepers implements Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:scare_creepers");
    }

    @Override
    public String description() {
        return "Creepers are scared of you and will only explode if you attack them first.";
    }

    @Override
    public String title() {
        return "Catlike Appearance";
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            fixCreeper(creeper);
        }
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Creeper creeper) {
                fixCreeper(creeper);
            }
        }
    }

    public void fixCreeper(Creeper creeper) {
        Bukkit.getMobGoals().addGoal(creeper, 0, OriginsReborn.getNMSInvoker().getCreeperAfraidGoal(creeper, this::hasAbility, livingEntity -> {
            String data = creeper.getPersistentDataContainer().get(hitByPlayerKey, PersistentDataType.STRING);
            if (data == null) {
                return false;
            }
            return data.equals(livingEntity.getUniqueId().toString());
        }));
    }

    private final NamespacedKey hitByPlayerKey = new NamespacedKey(OriginsReborn.getInstance(), "hit-by-player");

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.CREEPER) {
            org.bukkit.entity.Player player;
            if (event.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof org.bukkit.entity.Player shooter) player = shooter;
                else return;
            } else if (event.getDamager() instanceof org.bukkit.entity.Player damager) player = damager;
            else return;
            runForAbility(player, p -> p.getPersistentDataContainer().set(hitByPlayerKey, PersistentDataType.STRING, p.getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getType() == EntityType.CREEPER) {
            runForAbility(event.getTarget(), player -> {
                String data = event.getEntity().getPersistentDataContainer().get(hitByPlayerKey, PersistentDataType.STRING);
                if (data == null) {
                    event.setCancelled(true);
                    return;
                }
                if (!data.equals(player.getUniqueId().toString())) {
                    event.setCancelled(true);
                }
            });
        }
    }
}
