package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Jackhammer extends Kit {

    private static HashMap<Player, Integer> jackhammerUses = new HashMap<>();

    public Jackhammer(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(30.0D);
        setIcon(new ItemStack(Material.STONE_AXE));
        setDescription("§7Quebre um bloco e faça uma fenda até o fim do mundo.");
        setItems(new ItemCreator(Material.STONE_AXE).setType(Material.STONE_AXE).setName("§aJackhammer").getStack());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (hasKit(player) && player.getItemInHand() != null && isKitItem(event.getPlayer().getItemInHand(), "§aJackhammer")) {
            if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                player.sendMessage("§cVocê não pode usar o kit Jackhammer na invencibilidade.");
                return;
            }
            int x = event.getBlock().getX();
            int z = event.getBlock().getZ();
            if (x >= 489 || x <= -489 || z >= 489 || z <= -489) {
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode usar o kit Jackhammer aqui.");
                return;
            }
            if (inCooldown(player)) {
                sendCooldown(player);
                return;
            }
            if (jackhammerUses.containsKey(player)) {
                jackhammerUses.put(player, jackhammerUses.get(player) + 1);
            } else {
                jackhammerUses.put(player, 1);
            }

            if (jackhammerUses.get(player) == 6) {
                if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
                    breakBlock(event.getBlock(), BlockFace.UP);
                } else {
                    breakBlock(event.getBlock(), BlockFace.DOWN);
                }
                jackhammerUses.remove(player);
                addCooldown(player);
            } else {
                if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
                    breakBlock(event.getBlock(), BlockFace.UP);
                } else {
                    breakBlock(event.getBlock(), BlockFace.DOWN);
                }
            }
        }
    }

    private void breakBlock(final Block b, final BlockFace face) {
        new BukkitRunnable() {
            Block block = b;

            public void run() {
                if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME && block.getY() <= 128 && !block.hasMetadata("inquebravel")) {
                    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
                    block.setType(Material.AIR);
                    block = block.getRelative(face);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 2L, 2L);
    }
}
