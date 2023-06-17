package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Checkpoint extends Kit {

    public Checkpoint(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.NETHER_FENCE));
        setItems(new ItemCreator().setType(Material.NETHER_FENCE).setName("§aCheckpoint").addItemFlag(ItemFlag.values()).getStack());
        setDescription("§7Marque um ponto e volte para ele quando você quiser.");
    }

    public HashMap<UUID, Location> checkpoint = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled())
            return;
        if (!hasKit(e.getPlayer()))
            return;
        if (isKitItem(e.getPlayer().getItemInHand(), "§aCheckpoint")) {
            if (checkpoint.containsKey(e.getPlayer().getUniqueId()) && checkpoint.get(e.getPlayer().getUniqueId()).equals(e.getBlock().getLocation()))
                return;
            if (checkpoint.containsKey(e.getPlayer().getUniqueId())) {
                if (checkpoint.containsValue(e.getBlockAgainst()))
                    return;
                Block block = checkpoint.get(e.getPlayer().getUniqueId()).getWorld().getBlockAt(checkpoint.get(e.getPlayer().getUniqueId()));
                block.setType(Material.AIR);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.NETHER_FENCE.getId());
            }

            checkpoint.put(e.getPlayer().getUniqueId(), e.getBlock().getLocation());
            e.getPlayer().sendMessage("§aVocê marcou a localização, bata com o botão esquerdo para se teleportar.");
            e.getPlayer().setItemInHand(e.getPlayer().getItemInHand());
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!hasKit(e.getPlayer()))
            return;
        if (e.getItem() != null) {
            if (isKitItem(e.getItem(), "§aCheckpoint") && e.getAction().toString().contains("LEFT")) {
                e.setCancelled(true);
                if (checkpoint.containsKey(e.getPlayer().getUniqueId())) {
                    if (checkpoint.get(e.getPlayer().getUniqueId()).getWorld() != e.getPlayer().getWorld()) {
                        checkpoint.get(e.getPlayer().getUniqueId()).getWorld().getBlockAt(checkpoint.get(e.getPlayer().getUniqueId())).setType(Material.AIR);
                        checkpoint.remove(e.getPlayer().getUniqueId());
                        e.getPlayer().sendMessage("§cVocê não pode teleportar para outro mundo utilizando o kit Checkpoint.");
                        return;
                    }
                    if (e.getPlayer().getLocation().distance(checkpoint.get(e.getPlayer().getUniqueId())) > 100)
                        checkpoint.get(e.getPlayer().getUniqueId()).getWorld().strikeLightningEffect(checkpoint.get(e.getPlayer().getUniqueId()));
                    e.getPlayer().teleport(checkpoint.get(e.getPlayer().getUniqueId()));
                    checkpoint.get(e.getPlayer().getUniqueId()).getWorld().getBlockAt(checkpoint.get(e.getPlayer().getUniqueId())).setType(Material.AIR);
                    checkpoint.remove(e.getPlayer().getUniqueId());
                    e.getPlayer().sendMessage("§aTeleportado!");
                    e.getPlayer().updateInventory();
                } else {
                    e.getPlayer().sendMessage("§cVocê não possui nenhuma marcação.");
                }
            }
        }
    }


    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        if (e.getBlock().getType() == Material.NETHER_FENCE && checkpoint.containsValue(e.getBlock().getLocation())) {
            e.setCancelled(true);
            for (UUID p : checkpoint.keySet()) {
                if (checkpoint.get(p).equals(e.getBlock().getLocation())) {
                    Player o = Bukkit.getPlayer(p);
                    if (o != null)
                        o.sendMessage((o == e.getPlayer() ? "§cVocê destruiu seu checkpoint." : "§cSeu checkpoint foi destruído."));
                    checkpoint.get(p).getBlock().setType(Material.AIR);
                    checkpoint.get(p).getWorld().playEffect(checkpoint.get(p), Effect.STEP_SOUND, Material.NETHER_FENCE.getId());
                    checkpoint.remove(p);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (checkpoint.containsKey(e.getEntity().getUniqueId())) {
            checkpoint.get(e.getEntity().getUniqueId()).getBlock().setType(Material.AIR);
            checkpoint.remove(e.getEntity().getUniqueId());
        }
    }
}
