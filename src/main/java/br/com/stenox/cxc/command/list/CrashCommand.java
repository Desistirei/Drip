package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.utils.CrashAPI;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrashCommand extends CommandBase {

    public CrashCommand() {
        super("crash");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if(!hasPermission(sender, Group.OWNER))
            return false;

        if (args.length != 1){
            sender.sendMessage("§cUsage: /crash [player]");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cJogador inválido.");
            return false;
        }
        if (player.getName().equalsIgnoreCase("stenoxpvp")){
            sender.sendMessage("§cTa maluco porra");
            return false;
        }
        CrashAPI.crashPlayer(player);
        sender.sendMessage("§aJogador crashado.");
        return false;
    }
}
