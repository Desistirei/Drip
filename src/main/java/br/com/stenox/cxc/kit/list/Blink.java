package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Blink extends Kit {

    public Blink(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(20.0D);
        setIcon(new ItemStack(Material.NETHER_STAR));
        setDescription("§7Se locomova rapidamente criando folhas onde estiver olhando.");
        setItems(new ItemCreator().setMaterial(Material.NETHER_STAR).setName("§aBlink").getStack());
    }

    private HashMap<UUID, Integer> uses = new HashMap<UUID, Integer>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if ((e.getAction().name().contains("RIGHT")) && (hasKit(p)) && (hasDisplay(e.getItem(), "§aBlink"))) {
            e.setCancelled(true);
            if (Main.getInstance().getGame().getTime() >= 2400 && Main.getInstance().getGame().getStage() == GameStage.IN_GAME) {
                p.sendMessage("§cVocê não pode utilizar o kit Blink na arena final.");
                return;
            }
            if (inCooldown(p)) {
                sendCooldown(p);
                return;
            }
            Block b = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(5.0D)).getBlock();
            if (Gladiator.gladiatorA.containsKey(p)) {
                p.sendMessage("§cVocê não pode usar o kit Blink durante um duelo Gladiator.");
                return;
            }

            if (b.getY() > 128 && !Gladiator.gladiatorA.containsKey(p)) {
                p.sendMessage("§cVocê não pode utilizar o kit Blink nesta altura.");
                return;
            }
            int usou = 0;
            if (this.uses.containsKey(p.getUniqueId())) {
                usou = this.uses.get(p.getUniqueId());
            }
            usou++;
            if (usou == 3) {
                addCooldown(p);
                this.uses.remove(p.getUniqueId());
            } else {
                this.uses.put(p.getUniqueId(), usou);
            }
            if (b.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                Block down = b.getRelative(0, -1, 0);
                down.setType(Material.LEAVES);
            }
            p.teleport(new Location(p.getWorld(), b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch()));
            p.setFallDistance(0.0F);
            p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 2.0F);
            if (uses.get(p.getUniqueId()) == null) {
                return;
            }
            p.sendMessage("§aVocê utilizou o kit Blink (" + (3 - uses.get(p.getUniqueId()) + " usos restantes.)"));
        }
    }

}
