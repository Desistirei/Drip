package br.com.stenox.cxc.login.check;

import br.com.stenox.cxc.login.check.api.*;
import br.com.stenox.cxc.login.exception.InvalidCheckException;
import br.com.stenox.cxc.login.util.Util;
import br.com.stenox.cxc.Main;

import java.util.ArrayList;
import java.util.List;

public class Check {

	private static final List<Verify> VERIFY_LIST;

	public static boolean fastCheck(String playerName) throws InvalidCheckException {
		if (Util.hasClass("org.bukkit.Bukkit")) {
			if (Main.getInstance().getStorage().getPremiumMap().containsKey(playerName)) {
				return Main.getInstance().getStorage().getPremiumMap().get(playerName);
			}
		}
		for (Verify verify : Check.VERIFY_LIST) {
			boolean check = verify.verify(playerName);
			if (!verify.getResult()) {
				continue;
			}
			return check;
		}
		throw new InvalidCheckException("Impossible to check: " + playerName);
	}

	public enum CheckAPI {
		MOJANG_API("https://api.mojang.com/users/profiles/minecraft/"),
		MC_UUID("https://api.mcuuid.com/v1/uuid/"),
		MINECRAFT_API("https://minecraft-api.com/api/uuid/uuid.php?pseudo="),
		MINETOOLS("https://api.minetools.eu/uuid/"),
		MCAPI_CA("https://mcapi.ca/rawskin/");

		private final String link;

		CheckAPI(String link) {
			this.link = link;
		}

		public String getLink() {
			return this.link;
		}
	}

	static {
		VERIFY_LIST = new ArrayList<>();
		Check.VERIFY_LIST.add(new Mojang());
		Check.VERIFY_LIST.add(new MCUUID());
		Check.VERIFY_LIST.add(new MCAPICA());
		Check.VERIFY_LIST.add(new MinecraftAPI());
		Check.VERIFY_LIST.add(new MineTools());
	}

}
