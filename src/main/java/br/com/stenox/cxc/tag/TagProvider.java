package br.com.stenox.cxc.tag;

import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import br.com.stenox.cxc.provider.Provider;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.tag.listener.TagListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class TagProvider extends Provider {

    private Map<String, Tag> ntag;

    public TagProvider(final Main plugin) {
        super(plugin);
        this.ntag = new HashMap<>();
    }

    @Override
    public void onEnable() {
        this.ntag.clear();
        for (final Player o : Bukkit.getOnlinePlayers()) {
            this.ntag.put(o.getName(), Tag.MEMBER);
            this.update(o);
        }
        this.registerListener(new TagListener(this));
    }

    public void updateTag(Gamer gamer) {
        if (gamer == null) return;
        if (gamer.getPlayer() == null) return;

        Tag tag = gamer.getTag();
        if (tag == null)
            tag = Tag.MEMBER;

        if (gamer.getTeam() != null)
            tag = Tag.fromString(gamer.getTeam().getName());

        setTag(gamer.getPlayer(), tag);
    }

    public Tag getTag(final Player p) {
        return this.ntag.get(p.getName());
    }

    public Tag getTag(final Player p, Tag defaultTag) {
        return this.ntag.getOrDefault(p.getName(), defaultTag);
    }

    public boolean currentTag(final Player p, final Tag tag) {
        return this.ntag.getOrDefault(p.getName(), Tag.MEMBER) == tag;
    }

    public void removePlayerTag(final Player p) {
        p.setDisplayName(p.getName());
        p.getScoreboard().getTeams().forEach(t -> {
            if (t.getName().startsWith("tag:")) {
                t.unregister();
            }
        });

        for (Player on : Bukkit.getOnlinePlayers()) {
            if (!on.equals(p) && !on.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                on.getScoreboard().getTeams().forEach(t -> removeTeamEntry(t, p.getName()));
            }
        }

        this.ntag.remove(p.getName());
    }

    public void removeTeamEntry(Team team, String entry) {
        if (team.getName().startsWith("tag:")) {
            team.removeEntry(entry);
            if (team.getEntries().isEmpty()) {
                team.unregister();
            }
        }
    }

    public void setTag(final Player player, final Tag tag) {
        this.ntag.put(player.getName(), tag);
        this.update(player);
    }

    public void updateCurrent(final Player p) {
        this.update(p);
    }

    public void update(final Player p) {
        Tag tag = this.ntag.getOrDefault(p.getName(), Tag.MEMBER);
        String prefix = tag.getPrefix();
        String teamOrder = "tag:" + tag.getTeam() + p.getEntityId();

        p.setDisplayName(prefix + p.getName());

        final Team now = createTeamIfNotExists(p, p.getName(), teamOrder, prefix, "");
        now.setCanSeeFriendlyInvisibles(false);

        for (Team old : p.getScoreboard().getTeams()) {
            if (old.hasEntry(p.getName()) && !old.getName().equals(now.getName())) {
                old.unregister();
            }
        }

        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o == p)
                continue;
            Gamer online = Gamer.getGamer(o.getUniqueId());
            if (online == null)
                continue;

            Tag t = this.ntag.getOrDefault(o.getName(), Tag.MEMBER);

            String globalPrefix = t.getPrefix();
            String globalSuffix = "";

            String globalTeamOrder = "tag:" + t.getTeam() + o.getEntityId();

            final Team to = this.createTeamIfNotExists(p, o.getName(), globalTeamOrder, globalPrefix, globalSuffix);

            to.setCanSeeFriendlyInvisibles(false);

            for (Team old2 : p.getScoreboard().getTeams()) {
                if (old2.hasEntry(o.getName()) && !old2.getName().equals(to.getName())) {
                    old2.unregister();
                }
            }
            createTeamIfNotExists(o, p.getName(), now.getName(), now.getPrefix(), now.getSuffix());
        }
    }

    public Team createTeamIfNotExists(final Player p, final String entry, final String teamID, final String prefix, final String suffix) {
        Scoreboard scoreboard = p.getScoreboard();
        if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            p.setScoreboard(scoreboard);
        }
        Team team = scoreboard.getTeam(teamID);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamID);
        }

        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        return team;
    }

    @Override
    public void onDisable() {
        for (final Player o : Bukkit.getOnlinePlayers()) {
            this.removePlayerTag(o);
        }
        this.ntag.clear();
        this.ntag = null;
    }
}