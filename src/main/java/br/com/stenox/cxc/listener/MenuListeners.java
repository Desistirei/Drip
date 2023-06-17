package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.team.TeamSelectorMenu;
import br.com.stenox.cxc.utils.menu.Menu;
import br.com.stenox.cxc.utils.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MenuListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getInventory().getHolder() instanceof Menu){
            Menu menu = (Menu) event.getInventory().getHolder();
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (Main.getInstance().getGame().getStage() == GameStage.STARTING && player.getItemInHand() != null && player.getItemInHand().getType() == Material.NAME_TAG){
            new TeamSelectorMenu().open(new PlayerMenuUtility(player));
        }
    }
}
