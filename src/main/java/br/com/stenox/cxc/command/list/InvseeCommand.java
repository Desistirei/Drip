package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class InvseeCommand extends CommandBase {

    public InvseeCommand() {
        super("invsee", "", "", Collections.singletonList("inv"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender)){
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length != 1){
            sender.sendMessage("§cUsage: /" + lb + " [player]");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null){
            ((Player)sender).openInventory(target.getInventory());
            sender.sendMessage("§aAbrindo inventário...");
        } else {
            sender.sendMessage("§cJogador não encontrado.");
        }

        return false;
    }
}
