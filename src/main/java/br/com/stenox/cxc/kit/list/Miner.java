package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Miner extends Kit {

    public Miner(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.STONE_PICKAXE));
        setDescription("§7Seja um bom minerador e use suas habilidades para coletar minérios de forma rápida.");
        ItemStack item = createItemStack("§aMiner", Material.STONE_PICKAXE);
        item.addEnchantment(Enchantment.DURABILITY, 1);
        item.addEnchantment(Enchantment.DIG_SPEED, 1);
        setItems(item);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (hasKit(event.getPlayer())) {
            if (block.getType() == Material.IRON_ORE || block.getType() == Material.COAL_ORE) {
                event.setCancelled(true);

                Set<Block> treeBlocks = getConnectedBlocks(event.getBlock());
                treeBlocks.add(block);

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    for (Block treeBlock : treeBlocks) {
                        if (event.getPlayer().getInventory().firstEmpty() == -1) {
                            treeBlock.breakNaturally();
                        } else {
                            treeBlock.getDrops(event.getPlayer().getItemInHand()).forEach(item -> {
                                event.getPlayer().getInventory().addItem(item);
                            });
                            event.getPlayer().giveExp(event.getExpToDrop());
                            treeBlock.setType(Material.AIR);
                        }
                    }
                    treeBlocks.clear();
                });
            }
        }
    }

    private void getConnectedBlocks(Block block, Set<Block> results, List<Block> todo) {
        for (BlockFace face : BlockFace.values()) {
            Block b = block.getRelative(face);
            if (b.getType() == block.getType())
                if (results.add(b))
                    todo.add(b);
        }
    }

    private Set<Block> getConnectedBlocks(Block block) {
        Set<Block> set = new HashSet<>();
        LinkedList<Block> list = new LinkedList<>();
        list.add(block);
        while ((block = list.poll()) != null)
            getConnectedBlocks(block, set, list);
        return set;
    }
}
