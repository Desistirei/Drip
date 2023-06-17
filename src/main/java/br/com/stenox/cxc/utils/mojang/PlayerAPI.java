package br.com.stenox.cxc.utils.mojang;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerAPI {

    public static void changePlayerName(Player player, String name) {
        changePlayerName(player, name, true);
    }

    @SuppressWarnings("unchecked")
    public static void changePlayerName(Player player, String name, boolean respawn) {
        if (respawn)
            removeFromTab(player);
        try {
            Object minecraftServer = MinecraftReflection.getMinecraftServerClass().getMethod("getServer").invoke(null);
            Object playerList = minecraftServer.getClass().getMethod("getPlayerList").invoke(minecraftServer);
            Field f = playerList.getClass().getSuperclass().getDeclaredField("playersByName");
            f.setAccessible(true);
            Map<String, Object> playersByName = (Map<String, Object>) f.get(playerList);
            playersByName.remove(player.getName());
            WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
            Field field = profile.getHandle().getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(profile.getHandle(), name);
            field.setAccessible(false);
            playersByName.put(name, MinecraftReflection.getCraftPlayerClass().getMethod("getHandle").invoke(player));
            f.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (respawn)
            respawnPlayer(player);
    }

    public void addToTab(Player player, Collection<? extends Player> players) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
        try {
            Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle").invoke(player);
            Object getDisplayName = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName")
                    .invoke(entityPlayer);
            int ping = (int) MinecraftReflection.getEntityPlayerClass().getField("ping").get(entityPlayer);
            packet.getPlayerInfoDataLists().write(0,
                    Arrays.asList(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), ping,
                            NativeGameMode.fromBukkit(player.getGameMode()),
                            getDisplayName != null ? WrappedChatComponent.fromHandle(getDisplayName) : null)));
        } catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
        for (Player online : players) {
            if (!online.canSee(player))
                continue;
            try {
                Main.getInstance().getProtocolManager().sendServerPacket(online, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFromTab(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
        try {
            Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle").invoke(player);
            Object getDisplayName = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName").invoke(entityPlayer);
            int ping = (int) MinecraftReflection.getEntityPlayerClass().getField("ping").get(entityPlayer);
            packet.getPlayerInfoDataLists().write(0, Arrays.asList(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), ping, NativeGameMode.fromBukkit(player.getGameMode()), getDisplayName != null ? WrappedChatComponent.fromHandle(getDisplayName) : null)));
        } catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.canSee(player))
                continue;
            try {
                Main.getInstance().getProtocolManager().sendServerPacket(online, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void respawnPlayer(Player player) {
        respawnSelf(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player) || !online.canSee(player)) {
                continue;
            }
            if (online.canSee(player)) {
                online.hidePlayer(player);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    online.showPlayer(player);
                }, 3L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void respawnSelf(Player player) {
        List<PlayerInfoData> data = new ArrayList<>();
        try {
            Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle").invoke(player);
            Object getDisplayName = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName")
                    .invoke(entityPlayer);
            int ping = (int) MinecraftReflection.getEntityPlayerClass().getField("ping").get(entityPlayer);
            data.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), ping,
                    NativeGameMode.fromBukkit(player.getGameMode()),
                    getDisplayName != null ? WrappedChatComponent.fromHandle(getDisplayName) : null));
        } catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }

        PacketContainer addPlayerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        addPlayerInfo.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
        addPlayerInfo.getPlayerInfoDataLists().write(0, data);

        PacketContainer removePlayerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        removePlayerInfo.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
        removePlayerInfo.getPlayerInfoDataLists().write(0, data);

        PacketContainer respawnPlayer = new PacketContainer(PacketType.Play.Server.RESPAWN);
        respawnPlayer.getIntegers().write(0, player.getWorld().getEnvironment().getId());
        respawnPlayer.getDifficulties().write(0, Difficulty.valueOf(player.getWorld().getDifficulty().name()));
        respawnPlayer.getGameModes().write(0, NativeGameMode.fromBukkit(player.getGameMode()));
        respawnPlayer.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
        boolean isFlying = player.isFlying();
        try {
            Main.getInstance().getProtocolManager().sendServerPacket(player, removePlayerInfo);
            Main.getInstance().getProtocolManager().sendServerPacket(player, addPlayerInfo);
            Main.getInstance().getProtocolManager().sendServerPacket(player, respawnPlayer);
            player.teleport(player.getLocation());
            player.setFlying(isFlying);
            player.setExp(player.getExp());
            player.setLevel(player.getLevel());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        player.updateInventory();
    }

    public static void changePlayerSkinVS(Player player, String value, String signature, boolean respawn) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new WrappedSignedProperty(player.getName(), value, signature));
        if (respawn)
            respawnPlayer(player);
    }

    public static void removePlayerSkin(Player player) {
        removePlayerSkin(player, true);
    }

    public static void removePlayerSkin(Player player, boolean respawn) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        gameProfile.getProperties().clear();
        if (respawn) {
            respawnPlayer(player);
        }
    }

    public static String[] getFromPlayer(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[]{texture, signature};
    }

    public static String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateName(String username, UUID sender) {
        if (Gamer.getGamer(sender).getGroup().ordinal() <= Group.ADMIN.ordinal()) return true;
        if (username.length() < 3) return false;
        if (username.length() > 16) return false;
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
