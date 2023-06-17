package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.event.login.PlayerCheckEvent;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.login.check.Check;
import br.com.stenox.cxc.login.exception.InvalidCheckException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.HashMap;

public class AuthListeners implements Listener {

    @EventHandler
    private void onJoin2(PlayerJoinEvent event) {
        if (Main.getInstance().getStorage().getPremiumMap().get(event.getPlayer().getName()) == null) {
            try {
                Main.getInstance().getStorage().setPremium(event.getPlayer().getName(),
                        Check.fastCheck(event.getPlayer().getName()));
            } catch (InvalidCheckException e) {
                e.printStackTrace();
            }
        }

        PlayerCheckEvent checkEvent = new PlayerCheckEvent(event.getPlayer(), Main.getInstance().getStorage().getState(event.getPlayer().getName()));
        Main.getInstance().getServer().getPluginManager().callEvent(checkEvent);
    }

    @EventHandler
    private void onLeave2(PlayerQuitEvent event) {
        if (!Main.getInstance().getStorage().getPremiumMap().containsKey(event.getPlayer().getName())) {
            return;
        }

        Main.getInstance().getStorage().removeVerified(event.getPlayer().getName(), Main.getInstance().getStorage().getState(event.getPlayer().getName()));
    }

    private static final HashMap<Player, LoginType> LOGIN_MAP = new HashMap<>();

    public static HashMap<Player, LoginType> getLoginMap() {
        return LOGIN_MAP;
    }

    public enum LoginType {
        LOGIN, REGISTER
    }

    @EventHandler
    private void onCheck(PlayerCheckEvent event) {
        if (event.isCracked()) {
            Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());

            if (gamer.getPassword() != null) {
                getLoginMap().put(event.getPlayer(), LoginType.LOGIN);
            } else {
                getLoginMap().put(event.getPlayer(), LoginType.REGISTER);
            }
            Main.getInstance().getStorage().addNeedLogin(event.getPlayer().getName());
        }
    }

    @EventHandler
    private void onLog(PlayerLoginEvent event) {
        if (getLoginMap().containsKey(event.getPlayer())) {
            getLoginMap().remove(event.getPlayer(), getLoginMap().get(event.getPlayer()));
        }
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent event) {
        if (event.getPlayer().getName().length() > 20) {
            event.disallow(Result.KICK_OTHER, "§cSeu nome é muito grande!");
        }
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            Main.getInstance().getStorage().removeNeedLogin(event.getPlayer().getName());
        }
        if (getLoginMap().containsKey(event.getPlayer())) {
            getLoginMap().remove(event.getPlayer(), getLoginMap().get(event.getPlayer()));
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Main.getInstance().getStorage().needLogin((event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            event.getPlayer().teleport(event.getFrom());
        }
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Main.getInstance().getStorage().needLogin(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (Main.getInstance().getStorage().needLogin(event.getPlayer().getName())) {
            if (event.getMessage().startsWith("/register") || event.getMessage().startsWith("/login")) {
                event.setCancelled(false);
            } else {
                event.getPlayer().sendMessage("§cVocê não pode usar comandos enquanto não se autenticar.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onTime(TimeSecondEvent event){
        Main.getInstance().getStorage().getLoginList().forEach(playerName -> {
            Player player = Bukkit.getPlayer(playerName);
            Gamer gamer = Gamer.getGamer(player.getUniqueId());

            if (gamer.getPassword() != null){
                player.sendMessage("§cUsage: /login [password]");
            } else {
                player.sendMessage("§cUsage: /register [password] [password]");
            }
        });
    }
}
