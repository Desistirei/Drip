package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.Set;

public class Flash extends Kit {

    public Flash(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(18.0D);
        setIcon(new ItemStack(Material.REDSTONE_TORCH_ON));
        setDescription("§7Teleporte-se para onde você estiver olhando.");
        setItems(new ItemCreator().setMaterial(Material.REDSTONE_TORCH_ON).setName("§aFlash").getStack());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (hasKit(e.getPlayer()) && hasDisplay(e.getItem(), "§aTeleportar")) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                e.getPlayer().updateInventory();
                return;
            }
            if (e.getAction() != Action.RIGHT_CLICK_AIR) {
                return;
            }
            e.setCancelled(true);
            Player p = e.getPlayer();
            if (inCooldown(p)) {
                sendCooldown(p);
                return;
            }

            Block b = p.getTargetBlock((Set<Material>) null, 100);
            if (b == null || b.getType() == Material.AIR) {
                return;
            }
            BlockIterator list = new BlockIterator(p.getEyeLocation(), 0, 100);
            while (list.hasNext()) {
                p.getWorld().playEffect(list.next().getLocation(), Effect.ENDER_SIGNAL, 100);
            }
            Location loc = new Location(Bukkit.getWorld("world"), b.getLocation().getX(), b.getLocation().getY() + 1.5, b.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
            p.teleport(loc);
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            addCooldown(p);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (isKitItem(e.getItemInHand(), "§aFlash")) {
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }
    }
}