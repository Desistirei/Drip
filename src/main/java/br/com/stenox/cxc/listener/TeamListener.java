package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.event.game.GameChangeStageEvent;
import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeamListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Gamer gamer = Gamer.getGamer(e.getEntity().getUniqueId());
            Gamer other = Gamer.getGamer(e.getDamager().getUniqueId());
            if (gamer.getTeam() != null && other.getTeam() != null) {
                if (gamer.getTeam() == other.getTeam()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCompass(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().getType() == Material.COMPASS && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            if (Main.getInstance().getGame().getStage() == GameStage.STARTING)
                return;
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aTeleportador"))
                return;
            Player player = event.getPlayer();
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (!gamer.isAlive())
                return;
            if (event.getAction().name().contains("RIGHT")) {
                Player enemy = getEnemy(player);
                if (enemy == null) {
                    player.sendMessage("§cNenhum alvo encontrado.");
                    player.setCompassTarget(player.getWorld().getSpawnLocation());
                } else {
                    player.sendMessage("§cBússola apontando para o inimigo: " + enemy.getName() + ".");
                    player.setCompassTarget(enemy.getLocation());
                }
            } else if (event.getAction().name().contains("LEFT")) {
                Player enemy = getFriend(player);
                if (enemy == null) {
                    player.sendMessage("§cNenhum aliado encontrado.");
                    player.setCompassTarget(player.getWorld().getSpawnLocation());
                } else {
                    player.sendMessage("§aBússola apontando para o aliado: " + enemy.getName() + ".");
                    player.setCompassTarget(enemy.getLocation());
                }
            }
        }
    }

    public GameManager getGameProvider() {
        return Main.getInstance().getGameManager();
    }

    private Player getEnemy(Player player) {
        Player target = null;
        for (Gamer gamers : getGameProvider().getAlivePlayers()) {
            if (gamers == null || gamers.getPlayer() == null) continue;

            if (gamers.getTeam() != null && Gamer.getGamer(player.getUniqueId()).getTeam() != null) {
                if (gamers.getTeam() == Gamer.getGamer(player.getUniqueId()).getTeam())
                    continue;
            }
            if (gamers.getPlayer().isOnline()) {
                Player playerTarget = gamers.getPlayer();
                if (!playerTarget.equals(player)) {
                    if (playerTarget.getLocation().distance(player.getLocation()) >= 15.0D) {
                        if (target == null) {
                            target = playerTarget;
                        } else if (target.getLocation().distance(player.getLocation()) > playerTarget.getLocation()
                                .distance(player.getLocation())) {
                            target = playerTarget;
                        }
                    }
                }
            }
        }
        return target;
    }

    private Player getFriend(Player player) {
        Player target = null;
        for (Gamer gamers : getGameProvider().getAlivePlayers()) {
            if (gamers == null || gamers.getPlayer() == null) continue;
            if (gamers.getTeam() != null && Gamer.getGamer(player.getUniqueId()).getTeam() != null) {
                if (gamers.getTeam() != Gamer.getGamer(player.getUniqueId()).getTeam())
                    continue;
            }
            if (gamers.getPlayer().isOnline()) {
                Player playerTarget = gamers.getPlayer();
                if (!playerTarget.equals(player)) {
                    if (playerTarget.getLocation().distance(player.getLocation()) >= 15.0D) {
                        if (target == null) {
                            target = playerTarget;
                        } else if (target.getLocation().distance(player.getLocation()) > playerTarget.getLocation()
                                .distance(player.getLocation())) {
                            target = playerTarget;
                        }
                    }
                }
            }
        }
        return target;
    }

    @EventHandler
    public void onGameChangeStage(GameChangeStageEvent event) {
        if (event.getNewStage() == GameStage.INVINCIBILITY) {
            for (Gamer gamer : getGameProvider().getAlivePlayers()) {
                if (gamer.getTeam() == null) {
                    gamer.getPlayer().setHealth(0.0D);
                }
            }
        }
    }
}
