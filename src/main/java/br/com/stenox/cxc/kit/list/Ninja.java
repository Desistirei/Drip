package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Ninja extends Kit {

    public static HashMap<UUID, Player> target = new HashMap<>();

    public Ninja(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(5.0D);
        setIcon(new ItemStack(Material.NETHER_STAR));
        setDescription("§7Seja teleportado para o último jogador atacado por você.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Player && hasKit((Player) event.getDamager())) {
            target.put(event.getDamager().getUniqueId(), ((Player) event.getEntity()));
        }
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && target.containsKey(event.getPlayer().getUniqueId())) {
            if (inCooldown(event.getPlayer())) {
                sendCooldown(event.getPlayer());
                return;
            }

            if (inCombatCooldown(event.getPlayer())){
                sendCombatCooldown(event.getPlayer());
                return;
            }

            Player targetPlayer = target.get(event.getPlayer().getUniqueId());
            if (Gamer.getGamer(targetPlayer.getUniqueId()).getKitName().equalsIgnoreCase("Neo")) {
                event.getPlayer().sendMessage("§cVocê não pode usar seu kit em jogadores utilizando o kit Neo.");
                return;
            }

            Gamer target = Gamer.getGamer(targetPlayer.getUniqueId());

            if (!target.isAlive() || target.isVanish()) {
                event.getPlayer().sendMessage("§cO jogador não está online.");
                return;
            }

            if (targetPlayer.isOnline() && targetPlayer.getLocation().distance(event.getPlayer().getLocation()) > 65.0D) {
                event.getPlayer().sendMessage("§cO jogador está muito distante de você.");
            } else {
                event.getPlayer().teleport(targetPlayer.getLocation());
                Ninja.target.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage("§aTeleportado!");
                addCooldown(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (hasKit(player)) {
                if (inCombatCooldown(player))
                    removeCombatCooldown(player);

                addCombatCooldown(player, 1.0D);
            }
        }
    }
}
