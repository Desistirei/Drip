package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Magma extends Kit {

    public Magma(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.LAVA_BUCKET));
        setDescription("§7Tenha chances de deixar seus inimigos pegando fogo.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity())
                && (e.getCause() == DamageCause.LAVA || e.getCause().name().contains("FIRE"))) {
            if (e.getCause() == DamageCause.LIGHTNING && (Math.abs(e.getEntity().getLocation().getBlockX()) > 390
                    || Math.abs(e.getEntity().getLocation().getBlockZ()) > 390))
                return;
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        if (!Variable.COMBAT.isActive())
            return;
        if (!(e.getEntity() instanceof Player))
            return;
        if (!(e.getDamager() instanceof Player))
            return;
        if (Main.getInstance().getGame().getStage() == GameStage.STARTING)
            return;
        if (!Gamer.getGamer(e.getDamager().getUniqueId()).isAlive())
            return;
        if (hasKit(((Player) e.getEntity()))) {
            if (new Random().nextInt(100) <= 33) {
                e.getDamager().setFireTicks(90);
            }
        }
    }

    @EventHandler
    public void onUpdate(TimeSecondEvent event) {
        for (Gamer gamer : Main.getInstance().getGameManager().getAlivePlayers()) {
            Player p = gamer.getPlayer();
            if (hasKit(p)) {
                if (!p.getLocation().getBlock().getType().name().contains("WATER")) {
                    continue;
                }
                ((CraftPlayer) p).getHandle().damageEntity(DamageSource.DROWN, 3);
            }
        }
    }
}
