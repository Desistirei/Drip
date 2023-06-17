package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Cannibal extends Kit {

    public Cannibal(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.ROTTEN_FLESH));
        setDescription("§7Tenha chance de causar fome em seus adversários.");
    }

    @EventHandler
    public void hungry(EntityDamageByEntityEvent e) {
        if (!e.isCancelled()) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                if (hasKit((Player) e.getDamager()) && (new Random().nextInt(5) == 0)) {
                    Player p = (Player) e.getEntity();
                    p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1), true);
                }
            }
        }
    }
}
