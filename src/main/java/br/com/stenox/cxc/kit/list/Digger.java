package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Digger extends Kit {

    public Digger(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(30.0D);
        setIcon(new ItemStack(Material.DRAGON_EGG));
        setDescription("§7Cave um buraco e duele com seus adversários em grande buraco.");
        setItems(new ItemCreator(Material.DRAGON_EGG).setName("§aDigger").getStack());
    }

    public static String NOFALL_TAG = "nofall";

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.FALL) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (p.hasMetadata(NOFALL_TAG)) {
                    e.setCancelled(true);
                    p.removeMetadata(NOFALL_TAG, Main.getInstance());
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (hasKit(event.getPlayer()) && hasDisplay(item, "§aDigger")) {
            if (inCooldown(event.getPlayer())) {
                sendCooldown(event.getPlayer());
                event.setCancelled(true);
                return;
            }
            if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cVocê não pode usar o kit Digger na invencibilidade.");
                return;
            }
            if (Main.getInstance().getGame().getTime() >= 2398) {
                event.getPlayer().sendMessage("§cVocê não pode usar o kit Digger na arena final.");
                event.setCancelled(true);
                return;
            }
            if (event.getBlock().getLocation().getY() > 128) {
                event.getPlayer().sendMessage("§cVocê não pode utilizar o kit Digger acima do limite de construção: 128.");
                event.setCancelled(true);
                return;
            }
            if (Gladiator.gladiatorA.containsKey(event.getPlayer())) {
                event.getPlayer().sendMessage("§cVocê não pode utilizar o kit Digger durante uma batalha Gladiator.");
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            addCooldown(event.getPlayer());
            final Block b = event.getBlock();
            event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation(), Effect.STEP_SOUND, event.getBlock().getType().getId());
            b.setType(Material.AIR);
            int dist = (int) Math.ceil((6 - 1) / 2.0D);
            for (int y = -1; y >= -6; y--) {
                for (int x = -dist; x <= dist; x++) {
                    for (int z = -dist; z <= dist; z++) {
                        if (b.getY() + y > 0) {
                            Block block = b.getWorld().getBlockAt(b.getX() + x, b.getY() + y, b.getZ() + z);
                            if (block.getType() != Material.BEDROCK && block.getType() != Material.GLASS
                                    && block.getY() <= 128 && block.getType() != Material.ENDER_PORTAL_FRAME) {
                                block.setType(Material.AIR);
                                event.getPlayer().setMetadata(NOFALL_TAG, new FixedMetadataValue(Main.getInstance(), true));
                                for (Entity nearby : event.getPlayer().getNearbyEntities(8.0, 3.5, 8.0)) {
                                    if (nearby instanceof Player) {
                                        Player playersNearby = (Player) nearby;
                                        playersNearby.setMetadata(NOFALL_TAG, new FixedMetadataValue(Main.getInstance(), true));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
