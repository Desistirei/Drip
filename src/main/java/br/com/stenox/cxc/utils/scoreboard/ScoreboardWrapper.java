package br.com.stenox.cxc.utils.scoreboard;

import br.com.stenox.cxc.utils.scoreboard.sidebar.Sidebar;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class ScoreboardWrapper {

    private final Scoreboard bukkitScoreboard;
    private final Sidebar sidebar;

    public ScoreboardWrapper(Scoreboard bukkitScoreboard) {
        this.bukkitScoreboard = bukkitScoreboard;
        this.sidebar = new Sidebar(this, getObjective(DisplaySlot.SIDEBAR));
    }

    // ------

    public void removePlayerFromTeams(Player player) {
        bukkitScoreboard.getTeams().forEach(t -> {
            t.removeEntry(player.getName());
            if (t.getEntries().isEmpty()) {
                t.unregister();
            }
        });
    }

    public boolean removePlayerFromTeam(String teamName, Player player) {
        Team team = bukkitScoreboard.getTeam(teamName);
        if (team != null && team.hasEntry(player.getName())) {
            team.removeEntry(player.getName());
            if (team.getEntries().isEmpty()) {
                team.unregister();
            }
            return true;
        }
        return false;
    }

    public void destroyTeams(Predicate<Team> predicate) {
        bukkitScoreboard.getTeams().forEach(t -> {
            if (predicate.test(t)) {
                t.unregister();
            }
        });
    }

    public void unregisterObjective(DisplaySlot displaySlot) {
        Objective obj = bukkitScoreboard.getObjective(displaySlot.name());
        if (obj != null) {
            obj.unregister();
        }
    }

    public Objective getObjective(DisplaySlot displaySlot) {
        return getObjective(displaySlot, "dummy", null);
    }

    public Objective getObjective(DisplaySlot displaySlot, String criteria) {
        return getObjective(displaySlot, criteria, null);
    }

    public Objective getObjective(DisplaySlot displaySlot, String criteria, Consumer<Objective> onNewObjective) {
        Objective obj = bukkitScoreboard.getObjective(displaySlot.name());
        if (obj == null) {
            obj = bukkitScoreboard.registerNewObjective(displaySlot.name(), criteria);
            obj.setDisplaySlot(displaySlot);
            if (onNewObjective != null)
                onNewObjective.accept(obj);
        }
        return obj;
    }

    public Team getTeam(String name) {
        return getTeam(name, null);
    }

    public Team getTeam(String name, Consumer<Team> onNewTeamCreated) {
        Team team = bukkitScoreboard.getTeam(name);
        if (team == null) {
            team = bukkitScoreboard.registerNewTeam(name);
            if (onNewTeamCreated != null)
                onNewTeamCreated.accept(team);
        }
        return team;
    }

    public Set<Team> getTeams() {
        return bukkitScoreboard.getTeams();
    }
}
