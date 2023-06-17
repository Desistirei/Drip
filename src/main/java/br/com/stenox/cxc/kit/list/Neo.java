package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Neo extends Kit {

    public Neo(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.ARROW));
        setDescription("§7Seja imune a projeteis e a certas habilidades contra seus adversários.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity()) && e.getDamager() instanceof Projectile) {
            e.setCancelled(true);
            Projectile p = (Projectile) e.getDamager();
            if (p.getShooter() instanceof Player) {
                e.setCancelled(true);
            }
        }
    }
}
