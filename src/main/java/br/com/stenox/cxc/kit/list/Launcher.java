package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

public class Launcher extends Kit {

    public Launcher(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.SPONGE));
        setDescription("§7Use suas esponjas para ir até o além.");
        setItems(new ItemStack(Material.SPONGE, 20));
    }

    private enum LauncherDirection {
        UP;
    }

    private HashMap<Block, LauncherDirection> sponges = new HashMap<Block, LauncherDirection>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void launcher(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.SPONGE) {
            this.sponges.put(event.getBlock(), LauncherDirection.UP);
        }
    }

    @EventHandler
    public void onInteractLauncher(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            if (e.getAction() == Action.PHYSICAL) {
                if (e.getClickedBlock().getType() == Material.WOOD_PLATE) {
                    Block b = e.getClickedBlock();
                    Block sponge = b.getRelative(BlockFace.DOWN);
                    if (sponge.getType() == Material.SPONGE) {
                        e.setCancelled(true);
                        Vector v;
                        v = e.getPlayer().getLocation().getDirection().multiply(3.92).setY(1.05);
                        e.getPlayer().setVelocity(v);
                        noFall.add(e.getPlayer());
                    }
                }
            }
        }
    }

    @EventHandler
    public void launcher(EntityExplodeEvent event) {
        for (Block b : event.blockList()) {
            if (this.sponges.containsKey(b)) {
                this.sponges.remove(b);
            }
        }
    }

    @EventHandler
    public void launcher(BlockBreakEvent event) {
        if (this.sponges.containsKey(event.getBlock())) {
            this.sponges.remove(event.getBlock());
        }
    }

    public static HashSet<Player> noFall = new HashSet<>();

    @EventHandler
    public void launcher(final PlayerMoveEvent event) {
        if (this.sponges.containsKey(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN))
                && event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
                && event.getPlayer().isOnGround()
                && event.getPlayer().getLocation().getBlock().getType() != Material.WOOD_PLATE) {
            final Vector v = event.getPlayer().getVelocity();
            float v_x = 0.0F;
            float v_y = 3.9F;
            float v_z = 0.0F;
            v.setX(v_x);
            v.setY(v_y);
            v.setZ(v_z);
            event.getPlayer().setVelocity(v);
            noFall.add(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void launcher(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player
                && noFall.contains(event.getEntity())) {
            noFall.remove(event.getEntity());
            event.setCancelled(true);
        }
    }
}