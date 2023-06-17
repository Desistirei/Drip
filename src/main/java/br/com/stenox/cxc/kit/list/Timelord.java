package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.utils.ParticleAPI;
import br.com.stenox.cxc.Main;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Timelord extends Kit {

    private static List<UUID> lockPlayer = new ArrayList<>();

    public Timelord(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(30.0D);
        setIcon(new ItemStack(Material.WATCH));
        setDescription("§7Congele seus adversários no tempo durante 7 segundos.");
        setItems(new ItemCreator().setType(Material.WATCH).setName("§aTimelord").getStack());
    }

    public void freeze(Player player) {
        if (!lockPlayer.contains(player.getUniqueId())) {
            lockPlayer.add(player.getUniqueId());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 4));
            player.sendMessage("§cVocê foi congelado por um Timelord durante 7 segundos.");
        }
    }

    public void unfreeze(Player player) {
        if (lockPlayer.contains(player.getUniqueId())) {
            lockPlayer.remove(player.getUniqueId());
            player.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (lockPlayer.contains(p.getUniqueId())) {
            if (((event.getTo().getX() != event.getFrom().getX())
                    || (event.getTo().getZ() != event.getFrom().getZ()))) {
                event.setTo(event.getFrom());
                return;
            }
        }
    }

    @EventHandler
    public void playerKick(PlayerKickEvent e) {
        if (e.getReason().contains("Flying")) {
            e.setCancelled(true);
        }
        if (e.getLeaveMessage().contains("Flying")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (hasKit(player)) {
                if (isKitItem(player.getItemInHand(), "§aTimelord")) {

                    if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                        player.sendMessage("§cVocê não pode usar o kit Timelord na invencibilidade.");
                        e.setCancelled(true);
                        return;
                    }

                    if (inCooldown(player)) {
                        sendCooldown(player);
                        e.setCancelled(true);
                        return;
                    }
                    e.setCancelled(true);
                    addCooldown(player);
                    Location base = player.getLocation();
                    for (int angle = 0; angle < 360; angle += 18) {
                        double x = Math.sin(Math.toRadians(angle)) * 5;
                        double z = Math.cos(Math.toRadians(angle)) * 5;
                        ParticleAPI.spawnParticle(EnumParticle.CRIT, base.clone().add(x, 0.5, z));
                        ParticleAPI.spawnParticle(EnumParticle.CRIT, base.clone().add(x + 0.1, 0.5, z + 0.1));
                        ParticleAPI.spawnParticle(EnumParticle.CRIT, base.clone().add(x, 0.5, z));
                        ParticleAPI.spawnParticle(EnumParticle.CRIT, base.clone().add(x + 0.1, 0.5, z + 0.1));
                    }
                    for (Entity entity : player.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
                        if (entity instanceof Player) {
                            Player targetPlayer = (Player) entity;

                            if (!getGamer(targetPlayer).isAlive())
                                continue;

                            freeze(targetPlayer);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                                unfreeze(targetPlayer);
                            }, 20 * 7);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void combatPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            unfreeze((Player) event.getEntity());
        }
    }
}
