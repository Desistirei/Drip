package br.com.stenox.cxc.login.util;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.login.version.Version;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Util {

	public static String encode(String paramString) {
		byte[] encode = Base64.getEncoder().encode(paramString.getBytes());
		return new String(encode, StandardCharsets.UTF_8);
	}

	public static String decode(String paramString) {
		byte[] decode = Base64.getDecoder().decode(paramString.getBytes());
		return new String(decode, StandardCharsets.UTF_8);
	}

	public static void info(String string) {
		Main.getInstance().getLogger().info(string);
	}

	public static void severe(String string) {
		Main.getInstance().getLogger().severe(string);
	}

	public static List<Player> getOnlinePlayers() {
		final List<Player> list = new ArrayList<>();
		for (World world : Main.getInstance().getServer().getWorlds()) {
			list.addAll(world.getPlayers());
		}
		return list;
	}

	public static boolean hasClass(String string) {
		try {
			Class.forName(string);
			return true;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	public static Object networkList(SocketAddress socketAddress, String connectionMethod, String networkManager) {
		try {
			Class<?> minecraftServer = Class
					.forName("net.minecraft.server.v1_8_R3.MinecraftServer");
			Object serverInstance = minecraftServer.getMethod("getServer", (Class<?>[]) null).invoke(minecraftServer,
					(Object[]) null);
			Method serverConnection = serverInstance.getClass().getMethod(connectionMethod, (Class<?>[]) null);
			Object invokedConnection = serverConnection.invoke(serverInstance, (Object[]) null);
			Iterable<Object> networkList = (Iterable<Object>) Reflection.getField(networkManager, invokedConnection, 0);
			for (final Object nextNetwork : networkList) {
				Object address = nextNetwork.getClass().getMethod("getSocketAddress", (Class<?>[]) null)
						.invoke(nextNetwork, (Object[]) null);
				if (address.equals(socketAddress)) {
					return nextNetwork;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setOnlineMode(boolean online) {
		try {
			Class<?> minecraftServer = Class.forName("net.minecraft.server." + Version.getPackageVersion() + ".MinecraftServer");
			Object getServer = minecraftServer.getMethod("getServer").invoke(null);
			getServer.getClass().getMethod("setOnlineMode", boolean.class).invoke(getServer, online);
			Object server = minecraftServer.getDeclaredField("server").get(getServer);
			Field onlineField = server.getClass().getDeclaredField("online");
			onlineField.setAccessible(true);
			Object getOnline = onlineField.get(server);
			Field setValue = getOnline.getClass().getDeclaredField("value");
			setValue.setAccessible(true);
			setValue.set(getOnline, online);
		} catch (Exception e) {
			Util.info("Nao foi possivel setar o onlineMode como "+online+"!");
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
		}
	}

}
