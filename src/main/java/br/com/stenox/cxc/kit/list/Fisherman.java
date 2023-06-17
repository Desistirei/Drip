package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public class Fisherman extends Kit {

    public Fisherman(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.FISHING_ROD));
        setDescription("§7Fisgue seus adversários e puxe-os até você");
        setItems(new ItemCreator(Material.FISHING_ROD).setName("§aVara de pesca").getStack());
    }

    @EventHandler
    public void onFisherman(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!hasKit(player))
            return;
        player.getItemInHand().setDurability((short) 0);
        if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
            return;
        }
        if (event.getCaught() != null && Gamer.getGamer(event.getCaught().getUniqueId()).getKitName().equalsIgnoreCase("Neo")) {
            player.sendMessage("§cVocê não pode puxar jogadores utilizando o kit Neo.");
            return;
        }
        if (event.getCaught() != null) {
            if (event.getHook() != null)
                event.getHook().setBounce(true);
            Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), event.getCaught().getLocation().getYaw(),
                    event.getCaught().getLocation().getPitch());
            loc.setYaw(event.getCaught().getLocation().getYaw());
            loc.setPitch(event.getCaught().getLocation().getPitch());
            if (event.getState() == State.CAUGHT_ENTITY) {
                if (!(event.getCaught() instanceof Player)) {
                    return;
                }
                if (event.getCaught() == player)
                    return;
                event.getCaught().teleport(loc);
                Gamer caught = Gamer.getGamer(event.getCaught().getUniqueId());
                Gamer fisherman = Gamer.getGamer(player.getUniqueId());
                caught.setFighting(8);
                caught.setLastCombat(fisherman);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityAA(final EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if ((e.getDamager() instanceof Player)) {
            if (e.isCancelled()) {
                return;
            }
            final Player d = (Player) e.getDamager();
            if ((d.getItemInHand().getType() == Material.FISHING_ROD)) {
                d.getItemInHand().setDurability((short) 0);
                d.updateInventory();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof FishHook && e.getEntity() instanceof Player) {
            FishHook o = (FishHook) e.getDamager();
            if (o.getShooter() instanceof Player) {
                if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                    return;
                }
                EntityFishingHook a = ((CraftFish) o).getHandle();
                Entity target = ((CraftEntity) e.getEntity()).getHandle();
                a.hooked = target;
                if (Gamer.getGamer(target.getUniqueID()).getKitName().equalsIgnoreCase("Neo")) {
                    ((Player) o.getShooter()).sendMessage("§cVocê não pode puxar jogadores utilizando o kit Neo.");
                    return;
                }
                if (hasKit((Player) o.getShooter())) {
                    ((Player) o.getShooter()).sendMessage("§aVocê fisgou " + e.getEntity().getName() + "!");
                    PlayerFishEvent fish = new PlayerFishEvent((Player) e.getEntity(), a.hooked.getBukkitEntity(),
                            State.CAUGHT_ENTITY);
                    Bukkit.getPluginManager().callEvent(fish);
                }
                e.setCancelled(true);
            }
        }
    }
}
