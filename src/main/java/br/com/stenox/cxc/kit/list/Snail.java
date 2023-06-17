package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Snail extends Kit {

    public Snail(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.SOUL_SAND));
        setDescription("§7Tenha chances de deixar seus inimigos com lentidão.");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSnail(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) return;
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player snail = (Player) event.getDamager();
        if (!hasKit(snail)) {
            return;
        }
        if (player instanceof Player && new Random().nextInt(100) <= 33) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0), true);
        }
    }
}
