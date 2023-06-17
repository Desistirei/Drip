package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event){
        event.setMotd(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfiguration().getString("motd.linha1")) + "\n" +
                ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfiguration().getString("motd.linha2")));
    }
}