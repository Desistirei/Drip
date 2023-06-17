package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.event.player.PlayerChangeStateEvent;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.menu.PlayersMenu;
import br.com.stenox.cxc.gamer.state.GamerState;
import br.com.stenox.cxc.utils.Items;
import br.com.stenox.cxc.utils.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatorsListeners implements Listener {

    @EventHandler
    public void onPlayerChangeState(PlayerChangeStateEvent event){
        Gamer gamer = event.getGamer();

        if (event.getNewState() == GamerState.DEAD){
            Player player = gamer.getPlayer();

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                Items.DEAD.build(player);
            }, 5L);
        }
    }

    @EventHandler
    public void playersMenu(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        if (!gamer.isAlive()){
            ItemStack item = player.getItemInHand();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§aTeleportar para um Jogador")){
                new PlayersMenu(new PlayerMenuUtility(player)).open();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (!gamer.isAlive() && !gamer.isVanish())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (!gamer.isAlive() && !gamer.isVanish())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());
        if (gamer == null) return;
        if (!gamer.isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;

        Gamer gamer = Gamer.getGamer(event.getEntity().getUniqueId());
        if (!gamer.isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (!gamer.isAlive())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (!gamer.isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event){
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (!gamer.isAlive() || gamer.isVanish())
            event.setCancelled(true);
    }
}
