package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.Game;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.menu.KitSelectorMenu;
import br.com.stenox.cxc.utils.menu.PlayerMenuUtility;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class StartingListeners implements Listener {

    private final Game game;

    public StartingListeners(){
        this.game = Main.getInstance().getGame();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event){
        if (game.getStage() == GameStage.STARTING && event.getPlayer().getGameMode() != GameMode.CREATIVE){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if ((game.getStage() == GameStage.STARTING) && event.getPlayer().getGameMode() != GameMode.CREATIVE){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (game.getStage() == GameStage.STARTING || game.getStage() == GameStage.INVINCIBILITY || game.getStage() == GameStage.ENDING && event.getEntity() instanceof Player){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if (game.getStage() == GameStage.STARTING){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event){
        if (game.getStage() == GameStage.STARTING){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event){
        if (game.getStage() == GameStage.STARTING){
            event.setAmount(0);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (Main.getInstance().getGame().getStage() == GameStage.STARTING && player.getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aEscolha seu kit"))
            new KitSelectorMenu(new PlayerMenuUtility(player)).open();
    }
}
