package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.logs.LogType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VanishListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());
        if (gamer.isVanish()) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                player.openInventory(target.getInventory());

                Main.getInstance().getGamerProvider().getGamers().stream().filter(p -> p.getGroup().ordinal() < gamer.getGroup().ordinal()).forEach(o -> {
                    o.sendLogMessage(LogType.OPEN_INVENTORY, gamer.getName(), target.getName());
                });
            }
        }
    }
}
