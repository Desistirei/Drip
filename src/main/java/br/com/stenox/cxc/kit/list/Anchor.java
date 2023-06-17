package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class Anchor extends Kit {

    public ArrayList<UUID> anchoring = new ArrayList<>();

    public Anchor(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.ANVIL));
        setDescription("§7Seja uma âncora, não dê e nem receba knockback.");
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        if (anchoring.contains(e.getPlayer().getUniqueId())) {
            e.setVelocity(new Vector(0, -1, 0));
            anchoring.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnchor(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if ((!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if ((hasKit(player)) || (hasKit(damager))) {
            player.setVelocity(new Vector(0, -1, 0));
            anchoring.add(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                player.setVelocity(new Vector(0, 0, 0));
            });
        }
    }
}