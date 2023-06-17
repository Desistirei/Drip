package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListeners implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].contains(":") ||
                event.getMessage().startsWith("/me"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (gamer.isStaffChat()){
            Main.getInstance().getGamerProvider().getGamers().stream().filter(g -> g.isStaffMessages() && g.getPlayer() != null).forEach(gamers -> {
                gamers.sendMessage("§d[SC] " + gamer.getTag().getPrefix() + gamer.getName() + ": §f" + event.getMessage().replaceAll("%", "%%"));
            });
            event.setCancelled(true);
            return;
        }
        if (gamer.isMuted()){
            event.getPlayer().sendMessage("§cVocê está mutado " + (gamer.getMuteTime() == -1 ? "permanentemente" : "temporariamente") + ".");
            event.setCancelled(true);
            return;
        }
        if (!Main.CHAT && gamer.getGroup().ordinal() > Group.YOUTUBER_PLUS.ordinal()){
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cO chat está desativado.");
            return;
        }
        if (!gamer.isAlive() && !gamer.isVanish()){
            if (Main.getInstance().getGame().getStage() != GameStage.ENDING) {
                event.setCancelled(true);
                Main.getInstance().getGameManager().getDeadPlayers().forEach(g -> {
                    if (g.getPlayer() != null) {
                        g.sendMessage("§7[ESPECTADOR] " + gamer.getTag().getPrefix() + event.getPlayer().getName() + ": §f" + event.getMessage().replaceAll("%", "%%"));
                    }
                });
            } else {
                event.setFormat("§7[ESPECTADOR] " + gamer.getTag().getPrefix() + event.getPlayer().getName() + ": §f" + event.getMessage().replaceAll("%", "%%"));
            }
            return;
        }
        event.setFormat(gamer.getTag().getPrefix() + event.getPlayer().getName() + ": §f" + event.getMessage().replaceAll("%", "%%"));
    }
}
