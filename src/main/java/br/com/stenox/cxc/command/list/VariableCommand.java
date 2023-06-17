package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VariableCommand extends CommandBase {

    public VariableCommand() {
        super("variable", "", "", Collections.singletonList("var"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (hasPermission(sender, Group.YOUTUBER_PLUS)) {
            if (args.length == 0 || args.length > 2) {
                sender.sendMessage("§cUsage: /var [variable] [active]");
                return false;
            }
            Variable var = Variable.searchByName(args[0]);
            if (var == null) {
                sender.sendMessage("§cEssa variável não existe.");
                return false;
            }

            if (args.length == 1) {
                if (var.getValue() instanceof Boolean) {
                    var.setValue(!var.isActive());
                } else {
                    sender.sendMessage("§cVocê não pode fazer isso nessa variável.");
                }
            } else {
                Object value;

                if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))
                    value = Boolean.parseBoolean(args[1]);
                else if (isInt(args[1])) {
                    value = Integer.parseInt(args[1]);

                    if (var.equals(Variable.TIMINGS_INVINCIBILITY))
                        Main.LAST_INVINCIBILITY_TIME = Integer.parseInt(args[1]);
                } else if (isDouble(args[1]))
                    value = Double.parseDouble(args[1]);
                else
                    value = null;


                if (value != null)
                    var.setValue(value);
                else {
                    sender.sendMessage("§cO valor não pode ser nulo.");
                    return false;
                }

                if (var.equals(Variable.TEAMS_LIMIT)) {
                    Arrays.stream(GamerTeam.values()).forEach(team -> {
                        team.setEnabled(false);
                    });

                    for (int i = 0; i < (int)value; i++){
                        if (i > GamerTeam.values().length)
                            break;
                        GamerTeam.getByOrdinal(i).setEnabled(true);
                    }

                    for (GamerTeam team : GamerTeam.values()) {
                        if (!team.isEnabled() && !team.getAlivePlayers().isEmpty()){
                            for (Gamer gamer : team.getAlivePlayers()) {
                                gamer.setTeam(null);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> subCommands = new ArrayList<>();
        for (Variable value : Variable.values())
            subCommands.add(value.getName());

        if (args.length == 1) {
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();
            for (String s : subCommands) {
                if (StringUtil.startsWithIgnoreCase(s, lastWord))
                    list.add(s);
            }
            return list;
        }
        return Collections.emptyList();
    }
}
