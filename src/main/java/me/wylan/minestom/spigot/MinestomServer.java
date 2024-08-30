package me.wylan.minestom.spigot;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;    

public class MinestomServer {
    private static String cachedFavicon;

    public static void main(String[] args) {
        MinecraftServer.LOGGER.info("Java: " + Runtime.version());
        MinecraftServer.LOGGER.info("Name: " + "Potter");
        MinecraftServer.LOGGER.info("Minestom: " + "65f75bb059");
        MinecraftServer.LOGGER.info("Supported protocol: %d (%s)".formatted(MinecraftServer.PROTOCOL_VERSION, MinecraftServer.VERSION_NAME));

        PlayerSkin playerSkin = PlayerSkin.fromUsername("peqtw");

        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        GlobalEventHandler skinEvent = MinecraftServer.getGlobalEventHandler();
        skinEvent.addListener(PlayerSkinInitEvent.class, event -> {
            event.setSkin(playerSkin);
        });

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            int players = MinecraftServer.getConnectionManager().getOnlinePlayers().size();
            ResponseData responseData = event.getResponseData();
            responseData.setOnline(players);
            responseData.setDescription("Potter server");
            responseData.setVersion("Potter");
            if(!cachedFavicon.isEmpty()) {
                responseData.setFavicon("data:image/png;base64,"+cachedFavicon);
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerPreLoginEvent.class, event -> {
            int onlines = MinecraftServer.getConnectionManager().getOnlinePlayers().size();
            int maxPlayers = 5;

            if (onlines > maxPlayers) {
                event.getPlayer().kick(String.format("The server is full! (%d/%d)",onlines,maxPlayers));
            }
        });

        try {
            BufferedImage image = ImageIO.read(new File("./server-icon.png"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            cachedFavicon = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            outputStream.close();
        } catch (IOException e) {
            cachedFavicon = "";
        }


        minecraftServer.start("0.0.0.0", 25565);
    }
}
