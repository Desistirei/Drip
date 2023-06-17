package br.com.stenox.cxc.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListeners implements Listener {

    @EventHandler
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();

        if (event.getMessage().toLowerCase().startsWith("/me")){
            player.sendMessage("§cComando bloqueado.");
            event.setCancelled(true);
        }
    }
}
