package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.rule.Rule;
import br.com.stenox.cxc.rule.manager.RuleManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;

public class RulesCommand extends CommandBase {

    public RulesCommand() {
        super("rules", "", "", Arrays.asList("rule", "regras", "regra"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length < 2){
            if (args.length == 1){
                if (args[0].equalsIgnoreCase("list")){
                    RuleManager manager = getPlugin().getRuleManager();

                    sender.sendMessage("§cRegras: ");
                    for (Map.Entry<Integer, Rule> entry : manager.getRules().entrySet())
                        sender.sendMessage("§f" + entry.getKey() + " - §7" + entry.getValue().getText());
                    return true;
                }
            }
        } else {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    if (i != 2)
                        sb.append(" ");
                    sb.append(args[i]);
                }
                Rule rule = new Rule(sb.toString(), args[1]);
                getPlugin().getRuleManager().addRule(rule);

                sender.sendMessage("§aRegra adicionada na categoria §f'" + rule.getRuleType() + "'§a.");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!isInteger(args[1])) {
                    sender.sendMessage("§cNúmero inválido.");
                    return false;
                }
                int position = Integer.parseInt(args[1]);
                getPlugin().getRuleManager().removeRule(position);
                sender.sendMessage("§aRegra removida com sucesso.");
                return true;
            } else if (args[0].equalsIgnoreCase("load")){
                String ruleType = args[1];
                if (getPlugin().getRulesConfig().contains(ruleType)){
                    sender.sendMessage("§aCarregando...");
                    getPlugin().getRuleManager().loadRules(ruleType);
                    sender.sendMessage("§aRegras carregadas.");
                    return true;
                } else {
                    sender.sendMessage("§cNão foi achado nenhuma regra nessa categoria.");
                    return false;
                }
            }
        }
        sender.sendMessage("§cUsage:");
        sender.sendMessage("§c/" + lb + " add [type] [text]");
        sender.sendMessage("§c/" + lb + " remove [rule]");
        sender.sendMessage("§c/" + lb + " load [ruleType]");
        sender.sendMessage("§c/" + lb + " list");
        return false;
    }
}