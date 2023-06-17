package br.com.stenox.cxc.utils;

import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Manager {

    private static Manager instance;

    static {
        Manager.instance = new Manager();
    }

    public static Manager getInstance() {
        return Manager.instance;
    }

    public void sendWarn(Player player, final String message) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            Gamer gamer = Gamer.getGamer(all.getUniqueId());

            if (gamer.getGroup().ordinal() <= Group.ADMIN.ordinal() && !all.getName().equals(player.getName())) {
                all.sendMessage("§7[" + message + "§7]");
            }
        }
    }
}
