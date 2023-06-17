package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

import static org.bukkit.entity.EntityType.PLAYER;

public class GamerSearchListener implements Listener {

    @EventHandler
    public void onCompass(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().getType() == Material.COMPASS && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            if (Main.getInstance().getGame().getStage() == GameStage.STARTING)
                return;
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aTeleportador"))
                return;
            Player player = event.getPlayer();

            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (!gamer.isAlive()) return;

            Player target = getTarget(player);
            if (target == null) {
                event.getPlayer().sendMessage("§cNenhum jogador foi encontrado, bússola apontando para o spawn!");
                event.getPlayer().setCompassTarget(event.getPlayer().getWorld().getSpawnLocation());
            } else {
                event.getPlayer().sendMessage("§aBússola apontando para " + target.getName());
                event.getPlayer().setCompassTarget(target.getLocation());
            }
        }
    }

    private Player getTarget(Player player) {
        Player target = mechanic(player);
        if (target == null) {
            for (Gamer gamers : Main.getInstance().getGamerProvider().getAliveGamers()) {
                if (!gamers.isAlive() || gamers.getPlayer() == null)
                    continue;
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
        }
        return target;
    }

    public Player mechanic(Player player) {
        List<Entity> entities = player.getNearbyEntities(18, 10, 18);
        entities.removeIf(c -> c.getType() != PLAYER);
        entities.removeIf(c -> !Gamer.getGamer(c.getUniqueId()).isAlive());
        if (entities.size() >= 3)
            return (Player) entities.get(0);
        return null;
    }
}