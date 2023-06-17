package br.com.stenox.cxc.tag;

import br.com.stenox.cxc.gamer.group.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Tag {

    BEHH("Behh", "§4§lBEHH §4", "§4", "A", "BEHH", "behh33", "fundador"),
    OWNER("Dono", "§4§lDONO §4", "§4", "B", "OWNER", "Dono"),
    DEVELOPER("Desenvolvedor", "§4§lDEV §4", "§4", "C", "DEVELOPER", "dev", "desenvolvedor"),
    ADMIN("Admin", "§4§lADMIN §4", "§4", "D", "ADMIN", "adm", "administrador"),
    MOD_PLUS("Mod+", "§5§lMOD+ §5", "§5", "E", "MOD_PLUS", "mod+"),
    MOD("Mod", "§5§lMOD §5", "§5", "F", "MOD", "mod", "moderador"),
    TRIAL("Trial", "§5§lTRIAL §5", "§5", "G", "TRIAL", "trial"),
    BUILDER("Builder", "§2§lBUILDER §2", "§2", "H", "BUILDER", "build"),
    YOUTUBER_PLUS("Youtuber+", "§3§lYT+ §3", "§3", "I", "YOUTUBER_PLUS", "yt+", "ytbr+"),
    STREAMER_PLUS("Streamer+", "§3§lSTREAMER+ §3", "§3", "J", "STREAMER_PLUS", "streamer+", "streamerplus"),
    HELPER("Helper", "§9§lHELPER §9", "§9", "K", "HELPER", "helper", "ajudante"),
    YOUTUBER("Youtuber", "§b§lYT §b", "§b", "L", "YOUTUBER", "yt", "ytbr"),
    STREAMER("Streamer", "§b§lSTREAMER §b", "§b", "M", "STREAMER", "stream"),
    MINI_YOUTUBER("MiniYt", "§b§lMINIYT §b", "§b", "N", "MINI_YOUTUBER", "miniyt", "yt-"),
    DRIP("Drip", "§6§lDRIP §6", "§6", "O", "DRIP", "drip", "drip"),
    ELITE("Elite", "§c§lELITE §c", "§c", "P", "ELITE", "elite"),
    BETA("Beta", "§1§lBETA §1", "§1", "Q", "BETA", "beta"),
    HALLOWEEN("Halloween", "§5§lHALLOWEEN §5", "§5", "R", "HALLOWEEN", "halloween"),
    NATAL("Natal", "§c§lNATAL §c", "§c", "S", "NATAL", "natal"),
    ICE("Ice", "§b§lICE §b", "§b", "T", "ICE", "ice"),
    SNOW("Snow", "§9§lSNOW §9", "§9", "U", "SNOW", "snow"),
    DOIS_MIL_E_VINTE_DOIS("2022", "§b§l2022 §b", "§b", "V", "DOIS_MIL_E_VINTE_DOIS", "2022"),
    BOOSTER("Booster", "§d§lBOOSTER §d", "§d", "W", "BOOSTER", "booster"),
    BLUE("Azul", "§9", "§9", "X", "MEMBER"),
    RED("Vermelho", "§c", "§c", "X", "MEMBER"),
    GREEN("Verde", "§2", "§2", "X", "MEMBER"),
    PINK("Rosa", "§d", "§d", "X", "MEMBER"),
    YELLOW("Amarelo", "§e", "§e", "X", "MEMBER"),
    WHITE("Branco", "§f", "§f", "X", "MEMBER"),
    PURPLE("Roxo", "§5", "§5", "X", "MEMBER"),
    ORANGE("Laranja", "§6", "§6", "X", "MEMBER"),
    GRAY("Cinza", "§7", "§7", "X", "MEMBER"),
    BLACK("Preto", "§0", "§0", "X", "MEMBER"),
    MEMBER("Membro", "§7", "§7", "Y", "MEMBER", "member", "membro, nenhum");

    private final String name;
    @Setter
    private String prefix;
    private final String color;
    private final String team;
    private final String group;
    private final List<String> aliases;

    Tag(String name, String prefix, String color, String team, String group, String... aliases){
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.team = team;
        this.group = group;
        this.aliases = Arrays.asList(aliases);
    }

    public Group getGroup() {
        for (Group group : Group.values()) {
            if (group.toString().equals(this.group)){
                return group;
            }
        }
        return Group.MEMBER;
    }

    public static Tag fromString(final String name) {
        final Tag[] values = values();
        for (int length = values.length, i = 0; i < length; ++i) {
            final Tag tag = values[i];
            if (name.equalsIgnoreCase(tag.name())) {
                return tag;
            }
            for (final String alias : tag.getAliases()) {
                if (!alias.equalsIgnoreCase(name)) {
                    continue;
                }
                return tag;
            }
        }
        return null;
    }
}
