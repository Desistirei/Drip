package br.com.stenox.cxc.utils.mojang.disguise;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.utils.mojang.PlayerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SkinProvider {

    public static void setSkin(Player player, Property textures) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        GameProfile profile = nmsPlayer.getProfile();
        profile.getProperties().clear();

        if (textures != null)
            profile.getProperties().put("textures", textures);

        PlayerAPI.respawnPlayer(player);
    }

    private static void spawnPlayer(Player player, EntityPlayer nmsPlayer) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player) || online.canSee(player)) {
                sendPacket(online, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer));
            }

            if (!online.equals(player)) {
                sendPacket(online, new PacketPlayOutNamedEntitySpawn(nmsPlayer));
            }
        }
    }

    private static void despawnPlayer(Player player, EntityPlayer nmsPlayer) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player) || online.canSee(player)) {
                sendPacket(online, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer));
            }

            if (!online.equals(player)) {
                sendPacket(online, new PacketPlayOutEntityDestroy(nmsPlayer.getId()));
            }
        }
    }

    private static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private static String[] getFromUUID(String uuid) {
        try {
            URL url_1 = new URL(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String[] getPlayerSkin(Gamer gamer) {
        return getFromUUID(gamer.getUniqueId().toString());
    }

    public static String[] applyDefaultSkinIfNotNull(Gamer account) {
        String[] properties = getPlayerSkin(account);
        if (properties == null) return null;
        if (Bukkit.getPlayer(account.getUniqueId()) != null) {
            new BukkitRunnable() {
                public void run() { setSkin(account.getPlayer(), new Property("textures", properties[0], properties[1]));
                }
            }.runTask(Main.getInstance());
        }
        return properties;
    }
}
