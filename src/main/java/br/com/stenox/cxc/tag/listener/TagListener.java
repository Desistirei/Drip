package br.com.stenox.cxc.tag.listener;

import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.tag.TagProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagListener implements Listener {

    private TagProvider provider;

    public TagListener(final TagProvider provider) {
        this.provider = provider;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) {
        this.provider.removePlayerTag(event.getPlayer());
    }

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinFirst(final PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(this.provider.getServer().getScoreboardManager().getNewScoreboard());
    }*/

    @EventHandler
    public void onPlayerQuitListener(PlayerQuitEvent e) {
        Scoreboard board = e.getPlayer().getScoreboard();
        if (board != null) {
            for (Team t : board.getTeams()) {
                t.unregister();
                t = null;
            }
            for (Objective ob : board.getObjectives()) {
                ob.unregister();
                ob = null;
            }
            board = null;
        }
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @EventHandler
    public void onTimeSecond(TimeSecondEvent event){
        for (Player player : Bukkit.getOnlinePlayers())
            provider.update(player);
    }
}
