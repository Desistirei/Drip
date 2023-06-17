package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Turtle extends Kit {

    public Turtle(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.DIAMOND_CHESTPLATE));
        setDescription("§7Receba dano reduzido quando agachado.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player
                && hasKit((Player) event.getDamager()) && ((Player) event.getDamager()).isSneaking()) {
            event.setDamage(1.0D);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && hasKit((Player) event.getEntity())) {
            Player player = (Player) event.getEntity();
            if (!player.isSneaking()) {
                return;
            }
            if (event.getCause() == DamageCause.CONTACT) {
                return;
            } else if (event.getCause() == DamageCause.FALL || event.getCause().name().contains("EXPLOSION") || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.LIGHTNING || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.MAGIC || event.getCause() == DamageCause.PROJECTILE || event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.WITHER) {
                event.setDamage(2.0D);
            } else {
                event.setDamage(event.getDamage() / 2.0D);
            }
        }
    }
}
