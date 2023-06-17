package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Cookiemonster extends Kit {

    public Cookiemonster(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(7.0D);
        setIcon(new ItemStack(Material.COOKIE));
        setDescription("§7Coma cookies e ganhe regeneração II e velocidade II.");
        setItems(new ItemStack(Material.COOKIE, 5));
    }

    @EventHandler(ignoreCancelled = true)
    public void quebrar(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (e.getBlock().getType() == Material.LONG_GRASS) {
            if ((hasKit(p)) && (new Random().nextInt(3) == 0)) {
                p.getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.COOKIE));
            }
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        if ((e.getAction().name().contains("RIGHT")) && (e.getItem() != null)
                && (e.getItem().getType() == Material.COOKIE) && (hasKit(e.getPlayer()))) {
            e.setCancelled(true);
            Player p = e.getPlayer();

            if (inCooldown(p)) {
                sendCooldown(p);
                return;
            }

            addCooldown(p);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 8), 1), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);
            if (e.getItem().getAmount() > 1) {
                e.getItem().setAmount(e.getItem().getAmount() - 1);
            } else {
                p.setItemInHand(null);
            }
        }
    }
}
