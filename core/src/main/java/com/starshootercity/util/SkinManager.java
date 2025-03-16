package com.starshootercity.util;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.AbilityRegister;
import com.starshootercity.abilities.SkinChangingAbility;
import com.starshootercity.util.config.ConfigManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.model.MineSkinResponse;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.SkinVariant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class SkinManager implements Listener {

    public static SkinsRestorer skinsRestorer;
    private static String ip;
    private static final Map<Player, List<SkinChangingAbility>> currentAbilities = new HashMap<>();

    public static String getIP() {
        return ip;
    }

    public SkinManager() {
        skinsRestorer = SkinsRestorerProvider.get();
        createServer();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) updateSkin(player, false);
            }
        }.runTaskTimer(OriginsReborn.getInstance(), 0, 5);
    }

    public static void updateSkin(Player player, boolean force) {
        List<SkinChangingAbility> abilities = new ArrayList<>();
        boolean shouldChange = force;

        List<SkinChangingAbility> current = currentAbilities.computeIfAbsent(player, p -> Collections.emptyList());

        for (SkinChangingAbility ability : AbilityRegister.skinChangingAbilities) {
            if (ability.hasAbility(player) && ability.shouldApply(player)) {
                abilities.add(ability);
                if (!current.contains(ability)) shouldChange = true;
            } else if (current.contains(ability)) shouldChange = true;
        }

        if (!shouldChange) return;

        currentAbilities.put(player, abilities);

        if (abilities.isEmpty()) {
            try {
                skinsRestorer.getSkinApplier(Player.class).applySkin(player);
            } catch (DataRequestException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        abilities.sort(Comparator.comparingInt(value -> value.getPriority(player)));
        Bukkit.getScheduler().runTaskAsynchronously(OriginsReborn.getInstance(), () -> saveSkin(player, abilities));
        Bukkit.getScheduler().runTaskLaterAsynchronously(OriginsReborn.getInstance(), () -> {
            if (!upload(player)) {
                currentAbilities.remove(player);
            }
        }, 10);
    }

    private static Server server;

    public static void createServer() {
        int port = ConfigManager.getConfigValue(ConfigManager.Option.SKINSRESTORER_HOOK_PORT);
        server = new Server(port);

        ip = "%s:%s".formatted(ConfigManager.getConfigValue(ConfigManager.Option.SKINSRESTORER_HOOK_IP), port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new SimpleServlet()), "/");

        server.setHandler(context);

        new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void unload() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class SimpleServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("image/png");
            response.setStatus(HttpServletResponse.SC_OK);

            String uuid = request.getServletPath().split("\\.")[0];
            File imageFile = new File(OriginsReborn.getInstance().getDataFolder(), "skins/%s.png".formatted(uuid));

            try (FileInputStream fis = new FileInputStream(imageFile);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
        }
    }

    private static final Map<UUID, URL> playerSkins = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerSkins.put(event.getPlayer().getUniqueId(), event.getPlayer().getPlayerProfile().getTextures().getSkin());

        updateSkin(event.getPlayer(), true);
    }

    public static void saveSkin(Player player, List<SkinChangingAbility> abilities) {
        URL playerSkin = playerSkins.get(player.getUniqueId());
        if (playerSkin == null) return;

        File file = new File(OriginsReborn.getInstance().getDataFolder(), "skins/%s.png".formatted(player.getUniqueId().toString()));
        boolean ignored = file.getParentFile().mkdirs();

        try {
            Files.deleteIfExists(file.toPath());

            boolean ignored2 = file.createNewFile();

            InputStream is = playerSkin.openStream();

            BufferedImage image = ImageIO.read(is);
            for (SkinChangingAbility ability : abilities) ability.modifySkin(image, player);
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean upload(Player player) {
        URL url;
        try {
            url = URI.create("http://%s/%s".formatted(getIP(), player.getUniqueId().toString())).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            MineSkinResponse response = skinsRestorer.getMineSkinAPI().genSkin(url.toString(),
                    player.getPlayerProfile().getTextures().getSkinModel()
                            == PlayerTextures.SkinModel.CLASSIC
                            ? SkinVariant.CLASSIC : SkinVariant.SLIM
            );
            skinsRestorer.getSkinApplier(Player.class).applySkin(player, response.getProperty());
        } catch (DataRequestException e) {
            throw new RuntimeException(e);
        } catch (MineSkinException e) {
            return false;
        }
        return true;
    }
}
