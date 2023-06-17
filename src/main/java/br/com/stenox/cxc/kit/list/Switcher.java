package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Switcher extends Kit {

    public Switcher(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(2.0D);
        setIcon(new ItemStack(Material.SNOW_BALL));
        setDescription("§7Lance suas bolinhas de neve e troque de posição com seus inimigos.");
        setItems(new ItemCreator(Material.SNOW_BALL).setName("§aSwitcher").getStack());
    }

    @EventHandler
    public void snowBall(PlayerInteractEvent e) {
        if (hasDisplay(e.getItem(), "§aSwitcher") && hasKit(e.getPlayer())) {
            e.setCancelled(true);
            if (!e.getAction().toString().contains("RIGHT"))
                return;
            e.getPlayer().updateInventory();
            if (inCooldown(e.getPlayer())) {
                sendCooldown(e.getPlayer());
                return;
            }
            if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                e.getPlayer().sendMessage("§cVocê não pode usar o kit Switcher na invencibilidade.");
                return;
            }
            e.getPlayer().launchProjectile(Snowball.class);
            addCooldown(e.getPlayer());
        }
    }

    @EventHandler
    public void snowball(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
            Snowball snowball = (Snowball) event.getDamager();
            Player shooter = (Player) snowball.getShooter();
            if (!hasKit(shooter))
                return;

            if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                shooter.sendMessage("§cVocê não pode usar o kit Switcher na invencibilidade.");
                return;
            }

            if (!(snowball.getShooter() instanceof Player))
                return;
            if (getGamer((Player) event.getEntity()).getKitName().equalsIgnoreCase("neo"))
                return;
            Location shooterLoc = shooter.getLocation();
            shooter.teleport(event.getEntity().getLocation());
            event.getEntity().teleport(shooterLoc);
        }
    }
}
