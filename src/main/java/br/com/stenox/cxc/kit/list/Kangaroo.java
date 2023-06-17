package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kangaroo extends Kit {

    private static List<UUID> kangarooUses = new ArrayList<>();

    public Kangaroo(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(10.0D);
        setIcon(new ItemStack(Material.FIREWORK));
        setDescription("§7Use seu foguete para se locomover rapidamente pelo mapa.");
        setItems(new ItemCreator(Material.FIREWORK).setName("§aKangaroo").getStack());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (hasKit(player) && isKitItem(event.getItem(), Material.FIREWORK, "§aKangaroo")) {
                event.setCancelled(true);

                if (kangarooUses.contains(player.getUniqueId()))
                    return;

                if (inCooldown(player)) {
                    sendCooldown(player);
                    return;
                }

                Vector vector = player.getEyeLocation().getDirection();
                if (player.isSneaking()) {
                    vector = vector.multiply(1.92F).setY(0.5F);
                } else {
                    vector = vector.multiply(0.5F).setY(1F);
                }

                player.setFallDistance(-1.0F);
                player.setVelocity(vector);
                kangarooUses.add(player.getUniqueId());
            }
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (kangarooUses.contains(e.getPlayer().getUniqueId()) && (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR || e.getPlayer().isOnGround())) {
            kangarooUses.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!hasKit((Player) event.getEntity()))
            return;
        if (event.isCancelled())
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY)
            return;
        if (event.getDamager() instanceof Player && !getGamer((Player) event.getDamager()).isAlive())
            return;
        addCooldown((Player) event.getEntity(), getCooldownSeconds());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity()) && e.getCause() == DamageCause.FALL) {
            if (e.getDamage() > 7.0D) {
                e.setDamage(5.0D);
            } else if (e.getDamage() < 2.0D) {
                e.setCancelled(true);
            }
        }
    }
}
