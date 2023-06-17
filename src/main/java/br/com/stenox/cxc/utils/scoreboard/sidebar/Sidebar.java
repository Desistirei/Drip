package br.com.stenox.cxc.utils.scoreboard.sidebar;

import br.com.stenox.cxc.utils.scoreboard.ScoreboardWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Sidebar {

    private final ScoreboardWrapper scoreboardWrapper;
    private final Objective objective;
    private final Map<Integer, Row> rows = new HashMap<>();

    @Getter
    @Setter
    private boolean hided = false;

    public void setTitle(String text) {
        if (text == null) {
            throw new NullPointerException("Title cannot be null");
        } else if (text.length() > 32) {
            throw new IllegalArgumentException("Title too big");
        }
        this.objective.setDisplayName(text);
    }

    private static final int MAX_LINES = 15;

    public void updateRows(Consumer<List<String>> updateConsumer) {
        List<String> rows = new ArrayList<>(MAX_LINES);
        updateConsumer.accept(rows);
        this.setContent(rows);
    }

    public void setContent(List<String> content) {
        if (content == null) {
            throw new NullPointerException("Content cannot be null");
        }

        if (content.size() > MAX_LINES) {
            throw new IllegalStateException("Content cannot be longer than 15 lines");
        }

        // Limpa a scoreboard
        if (content.isEmpty()) {
            rows.forEach((key, row) -> row.destroy());
            rows.clear();
            return;
        }

        ListIterator<String> iterator = content.listIterator(content.size());

        for (int x = 1; x <= MAX_LINES; x++) {
            if (iterator.hasPrevious()) {
                this.setText(x, iterator.previous());
            } else {
                this.removeText(x);
            }
        }
    }

    public void setText(int rowNumber, String text) {
        this.validatePosition(rowNumber);

        if (text == null) {
            throw new NullPointerException("Text cannot be null");
        }

        Row row = rows.computeIfAbsent(rowNumber, v -> createRow(rowNumber));

        String prefix = text;
        String suffix = "";

        if (text.length() > 16) {
            int substr = (text.charAt(15) == ChatColor.COLOR_CHAR) ? 15 : 16;

            prefix = text.substring(0, substr);

            String lastColors = ChatColor.getLastColors(prefix);
            suffix = lastColors + text.substring(substr);

            if (suffix.length() > 16) suffix = suffix.substring(0, 16);
        }

        row.setPrefix(prefix);
        row.setSuffix(suffix);
    }

    public void removeText(int position) {
        Row row = rows.remove(position);

        if (row != null) {
            row.destroy();
        }
    }

    public void hide() {
        if (isHided()) {
            return;
        }
        setHided(true);
        if (objective != null) {
            objective.unregister();
        }
        for (int i = 1; i < 16; i++) {
            Team team = scoreboardWrapper.getTeam("sb_row: " + i);
            if (team != null) {
                team.unregister();
            }
        }
    }

    public void cleanup() {
        if (!rows.isEmpty()) {
            rows.values().forEach(Row::destroy);
            rows.clear();
        }
    }

    public boolean isInternalUsageTeam(Team team) {
        return team != null && team.getName().startsWith("sb_row:");
    }

    private static final ChatColor[] colors = ChatColor.values();

    private Row createRow(int position) {
        String score = colors[position - 1] + "" + ChatColor.RESET;
        String teamName = "sb_row:" + position;

        this.objective.getScore(score).setScore(position);

        Team team = scoreboardWrapper.getTeam(teamName, cteam -> {
            cteam.addEntry(score);
        });

        return new Row(score, team);
    }

    private void validatePosition(int position) {
        if (position < 1 || position > 15) {
            throw new IllegalArgumentException("Scoreboard Row position \"" + position + "\" is invalid!");
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class Row {

        private final String score;
        private final Team team;

        public void destroy() {
            team.getScoreboard().resetScores(score);
            team.unregister();
        }

        public void setPrefix(String prefix) {
            team.setPrefix(prefix);
        }

        public void setSuffix(String suffix) {
            team.setSuffix(suffix);
        }

    }

    public interface Update {

    }

}
