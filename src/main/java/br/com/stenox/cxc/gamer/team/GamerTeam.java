package br.com.stenox.cxc.gamer.team;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum GamerTeam {

    RED("Vermelho", ChatColor.RED, Color.RED, true),
    BLUE("Azul", ChatColor.BLUE, Color.BLUE, true),
    GREEN("Verde", ChatColor.GREEN, Color.GREEN, false),
    PINK("Rosa", ChatColor.LIGHT_PURPLE, Color.FUCHSIA, false),
    YELLOW("Amarelo", ChatColor.YELLOW, Color.YELLOW, false),
    WHITE("Branco", ChatColor.WHITE, Color.WHITE, false),
    PURPLE("Roxo", ChatColor.DARK_PURPLE, Color.PURPLE, false),
    ORANGE("Laranja", ChatColor.GOLD, Color.ORANGE, false),
    GRAY("Cinza", ChatColor.GRAY, Color.GRAY, false),
    BLACK("Preto", ChatColor.BLACK, Color.BLACK, false);

    private final String name;
    private final ChatColor color;
    private final Color leatherColor;

    @Setter
    private boolean enabled;

    public List<Gamer> getAlivePlayers(){
        return Main.getInstance().getGamerProvider().getAliveGamers().stream().filter(gamer -> gamer.getTeam() == this).collect(Collectors.toList());
    }

    public static GamerTeam getByOrdinal(int ordinal){
        for (GamerTeam team : values()) {
            if (team.ordinal() == ordinal){
                return team;
            }
        }

        return null;
    }
}