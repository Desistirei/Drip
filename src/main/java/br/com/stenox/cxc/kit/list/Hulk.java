package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Hulk extends Kit {

    public Hulk(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(10.0D);
        setIcon(new ItemStack(Material.SADDLE));
        setDescription("§7Levante seus inimigos em sua cabeça e consiga lançá-los com sua super força.");
    }

    @EventHandler
    public void hulk(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player clicked = (Player) event.getRightClicked();
            if (!player.isInsideVehicle() && !clicked.isInsideVehicle() && player.getItemInHand().getType() == Material.AIR && hasKit(player)) {

                if (player.getLocation().getY() > 148) {
                    player.sendMessage("§cVocê não pode utilizar o kit Hulk nesta altura.");
                    return;
                }
                if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                    player.sendMessage("§cVocê não pode utilizar o kit Hulk na invencibilidade.");
                    return;
                }

                if (inCooldown(player)) {
                    sendCooldown(player);
                    return;
                }

                addCooldown(player);
                player.setPassenger(clicked);
            }
        }
    }

    @EventHandler
    public void onClientDie(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if (player.getPassenger() != null) {
            player.getPassenger().eject();
            player.eject();
        }
        if (player.getKiller() != null) {
            if (player.getPassenger() != null) {
                player.getPassenger().eject();
                player.eject();
            }
        }
    }

    @EventHandler
    public void noHulkMor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player hulk = (Player) event.getDamager();
            if (hulk.getPassenger() != null && hulk.getPassenger() == player && hasKit(hulk)
                    && hulk.getPassenger() == player) {
                player.setSneaking(true);

                Vector v = player.getEyeLocation().getDirection().multiply(1.5F);
                v.setY(0.92D);
                player.setVelocity(v);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.setSneaking(false), 10L);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.hasItem() && event.getPlayer().getItemInHand().getType() == Material.AIR) {
            if (player.getPassenger() != null) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    Vector vector = player.getVelocity();
                    player.getPassenger().leaveVehicle();
                    player.setVelocity(vector.multiply(1.75));
                    player.getPassenger().setFallDistance(2.5F);
                    player.sendMessage("§aVocê lançou o jogador " + player.getPassenger().getName() + " para longe.");
                }
            }
        }
    }
}
