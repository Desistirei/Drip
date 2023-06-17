package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayerFinderCommand extends CommandBase {

    public PlayerFinderCommand() {
        super("playerfinder", "", "", Collections.singletonList("pf"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD))
            return false;
        if (args.length != 1){
            sender.sendMessage("§cUsage: /" + lb + " [player]");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cAlvo não encontrado.");
            return false;
        }
        sender.sendMessage("§aProcurando...");
        List<String> nicks = Main.getInstance().getGamerRepository().fetch(player.getAddress().getAddress().toString());
        sender.sendMessage("§e" + nicks.get(0));
        for (int i = 1; i < nicks.size(); i++){
            sender.sendMessage("§e" + nicks.get(i));
        }
        return false;
    }
}
