package br.com.stenox.cxc.variable;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Variable {

    BUILD("build", true),
    DAMAGE("damage", true),
    COMBAT("combat", true),
    DROPS("drops", true),
    ALLOW_BUCKETS("allow_buckets", true),
    BONUS_FEAST("bonus_feast", false),
    FEAST("feast", true),
    MINI_FAST("mini_feast", true),
    ALL_KITS_FREE("kits_free", false),
    FINAL_BATTLE("final_battle", true),
    RESPAWN("respawn", true),
    TIMINGS_FEAST("timings.feast", 600),
    TIMINGS_INVINCIBILITY("timings.invincibility", 60),
    TIMINGS_BONUS_FEAST("timings.bonusfeast", 1560),
    TIMINGS_FINAL_ARENA("timings.finalarena", 2400),
    TEAMS_LIMIT("teams.limit", 2);

    private String name;

    private Object value;

    public void setValue(Object value) {
        this.value = value;
        String message = "";

        if (value instanceof Boolean)
            message = (boolean) value ? "true" : "false";

        if (value instanceof Double)
            message = Double.toString((Double) value);

        if (value instanceof Integer)
            message = Integer.toString((Integer) value);

        for (Gamer gamer : Main.getInstance().getGamerProvider().getGamers()) {
            if (gamer.getGroup().ordinal() <= Group.TRIAL.ordinal()){
                gamer.sendMessage("§b" + name + " §efoi alterado para §6" + message + ".");
            }
        }
    }

    public boolean isActive(){
        if (value instanceof Boolean){
            return (boolean) value;
        }
        return false;
    }

    public static Variable searchByName(String name){
        return Arrays.stream(values()).filter(variable -> variable.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
