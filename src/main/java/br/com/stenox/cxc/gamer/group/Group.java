package br.com.stenox.cxc.gamer.group;

import br.com.stenox.cxc.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Group {

    BEHH("Behh", "§4§lBEHH §4", "§4", Tag.BEHH, "behh"),
    OWNER("Dono", "§4§lDONO §4", "§4", Tag.OWNER, "dono", "owner"),
    DEVELOPER("Desenvolvedor", "§4§lDEV §4", "§4", Tag.DEVELOPER, "dev", "developer", "desenvolvedor"),
    ADMIN("Admin", "§4§lADMIN §4", "§4", Tag.ADMIN, "admin", "adm"),
    MOD_PLUS("Mod+", "§5§lMOD+ §5", "§5", Tag.MOD_PLUS, "mod+"),
    MOD("Mod", "§5§lMOD §5", "§5", Tag.MOD, "mod"),
    TRIAL("Trial", "§d§lTRIAL §d", "§b", Tag.TRIAL, "trial"),
    YOUTUBER_PLUS("Youtuber+", "§3§lYT+ §3", "§e", Tag.YOUTUBER_PLUS, "youtuber+", "yt+"),
    STREAMER_PLUS("Streamer+", "§3§lSTREAMER+ §3", "§3", Tag.STREAMER_PLUS, "streamer+"),
    HELPER("Helper", "§9§lHELPER §9", "§9", Tag.HELPER, "helper", "ajudante"),
    BUILDER("Builder", "§2§lBUILDER §2", "§2", Tag.BUILDER, "build", "builder"),
    YOUTUBER("Youtuber", "§b§lYOUTUBER §b", "§b", Tag.YOUTUBER, "youtuber", "yt"),
    STREAMER("Streamer", "§b§lSTREAMER §b", "§b", Tag.STREAMER, "streamer", "stream"),
    MINI_YOUTUBER("MiniYt", "§b§lMINIYT §b", "§b", Tag.MINI_YOUTUBER, "miniyt"),
    DRIP("Drip", "§6§lDRIP§6", "§6", Tag.DRIP, "drip"),
    ELITE("Elite", "§c§lELITE §c", "§c", Tag.ELITE, "elite"),
    BETA("Beta", "§1§lBETA §1", "§1", Tag.BETA, "beta"),
    HALLOWEEN("HALLOWEEN", "§5§lHALLOWEEN §5", "§6", Tag.HALLOWEEN, "halloween"),
    NATAL("Natal", "§c§lNATAL §c", "§c", Tag.NATAL, "natal"),
    ICE("Ice", "§b§lICE §b", "§b", Tag.ICE, "ice"),
    SNOW("Snow", "§9§lsnow §9", "§9", Tag.SNOW, "snow"),
    DOIS_MIL_E_VINTE_DOIS("2022", "§b§l2022 §b", "§b", Tag.DOIS_MIL_E_VINTE_DOIS, "2022"),
    BOOSTER("Booster", "§d§lBOOSTER §d", "§d", Tag.BOOSTER, "booster"),
    MEMBER("Membro", "§7", "§7", Tag.MEMBER, "normal", "membro");

    private final String name;
    private final String prefix;
    private final String color;
    private final Tag tag;
    private List<String> aliases;

    Group(String name, String prefix, String color, Tag tag, String... aliases) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.tag = tag;
        this.aliases = Arrays.asList(aliases);
    }

    public static Group getGroupByName(String name) {
        for (Group group : Group.values()) {
            for (String alias : group.getAliases()) {
                if (name.equalsIgnoreCase(alias)) {
                    return group;
                }
            }
        }
        return null;
    }
}
