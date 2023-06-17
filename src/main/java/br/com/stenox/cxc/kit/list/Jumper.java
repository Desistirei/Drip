package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Jumper extends Kit {

    public Jumper(GameManager gameManager) {
        super(gameManager);
        setActive(true);
        setCooldownSeconds(6.0D);
        setIcon(new ItemStack(Material.ENDER_PEARL));
        setDescription("§7Use sua Ender Pearl para se locomover entre grandes distâncias pelo mapa.");
        setItems(new ItemCreator(Material.ENDER_PEARL).setName("§aJumper").getStack());
    }

    @EventHandler
    public void onInteractEnderPearl(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT"))
            return;
        if (e.getItem() == null)
            return;
        if (e.getItem().getType() != Material.ENDER_PEARL)
            return;
        if (isKitItem(e.getItem(), "§aJumper") && hasKit(e.getPlayer())) {
            e.setCancelled(true);
            Player p = e.getPlayer();

            if (inCooldown(p) || inCooldown(p)) {
                sendCooldown(p);
                return;
            }
            addCooldown(p);
            p.setFallDistance(0);
            EnderPearl enderPearl = p.launchProjectile(EnderPearl.class);
            Vector vector = enderPearl.getVelocity();
            enderPearl.setVelocity(vector);
            enderPearl.setMetadata("Jumper", new FixedMetadataValue(Main.getInstance(), true));
            enderPearl.setPassenger(p);
            Launcher.noFall.add(p);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!hasKit((Player) event.getEntity()))
            return;
        if (event.isCancelled())
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY)
            return;
        if (event.getDamager() instanceof Player && !getGamer((Player) event.getDamager()).isAlive())
            return;
        addCooldown((Player) event.getEntity(), 6);
    }
}
