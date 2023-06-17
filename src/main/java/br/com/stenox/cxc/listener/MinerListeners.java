package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MinerListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !Variable.BUILD.isActive() || Main.LOCK || (Main.getInstance().getGame().getStage() == GameStage.STARTING && event.getPlayer().getGameMode() == GameMode.SURVIVAL))
            return;

        Block block = event.getBlock();

        if (block.getType() == Material.STONE || block.getType().name().contains("MUSHROOM") || block.getType() == Material.COCOA) {
            if (Main.getInstance().getGame().getStage() == GameStage.STARTING) return;
            if (Utils.firstPartial(event.getPlayer().getInventory(), block.getTypeId()) > -1) {
                event.getBlock().getDrops(event.getPlayer().getItemInHand()).forEach(item -> {
                    event.getPlayer().getInventory().addItem(item);
                });
                event.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType().name().contains("MUSHROOM")) {
            event.setCancelled(true);
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
