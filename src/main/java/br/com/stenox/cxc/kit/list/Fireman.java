package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Fireman extends Kit {

    public Fireman(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.WATER_BUCKET));
        setDescription("§7Seja imune a toda e qualquer alta temperatura.");
        setItems(new ItemStack(Material.WATER_BUCKET));
    }

    @EventHandler
    public void onFireman(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player fireman = (Player) event.getEntity();
        if (!hasKit(fireman))
            return;
        DamageCause fire = event.getCause();
        if (fire == DamageCause.FIRE || fire == DamageCause.LAVA || fire == DamageCause.FIRE_TICK || fire == DamageCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }
}
