package br.com.stenox.cxc.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class TitleAPI {

    public static void clear(Player player) {
        send(player, "", "", 1, 0, 1);
    }

    public static void sendAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, title, subtitle, fadeIn, stay, fadeOut));
    }

    public static void send(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PacketContainer resetPacket = new PacketContainer(PacketType.Play.Server.TITLE);
        resetPacket.getTitleActions().write(0, EnumWrappers.TitleAction.RESET);
        resetPacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

        PacketContainer titlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
        titlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
        titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

        PacketContainer subtitlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
        subtitlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
        subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(subtitle));

        PacketContainer timesPacket = new PacketContainer(PacketType.Play.Server.TITLE);
        timesPacket.getTitleActions().write(0, EnumWrappers.TitleAction.TIMES);
        StructureModifier<Integer> times = timesPacket.getIntegers();
        times.write(0, fadeIn);
        times.write(1, stay);
        times.write(2, fadeOut);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, resetPacket);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, titlePacket);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, subtitlePacket);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, timesPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
