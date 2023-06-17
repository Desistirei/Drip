package br.com.stenox.cxc.utils;

import java.lang.reflect.InvocationTargetException;

import br.com.stenox.cxc.utils.protocol.ProtocolGetter;
import br.com.stenox.cxc.utils.protocol.ProtocolVersion;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ActionBarAPI {

    public static void send(Player player, String text) {
        ProtocolVersion version = ProtocolGetter.getVersion(player);

        if (version.getId() >= ProtocolVersion.MINECRAFT_1_8.getId()) {
            PacketContainer chatPacket = new PacketContainer(PacketType.Play.Server.CHAT);
            chatPacket.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + " \"}"));
            chatPacket.getBytes().write(0, (byte) 2);

            try {
                Main.getInstance().getProtocolManager().sendServerPacket(player, chatPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet " + chatPacket, e);
            }
        }
    }

    public static void broadcast(String text) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, text));
    }
}