package br.com.stenox.cxc.command.list.punishments;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class UnmuteCommand extends CommandBase {

    public UnmuteCommand() {
        super("unmute");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD))
            return false;
        if (args.length != 1){
            sender.sendMessage("§cUsage: /unmute [player]");
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cJogador não encontrado.");
            return false;
        }

        Gamer gamer;

        if (player.isOnline()){
            gamer = Gamer.getGamer(player.getUniqueId());
        } else {
            gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());
        }
        if (gamer == null){
            sender.sendMessage("§cGamer não encontrado.");
            return false;
        }
        gamer.unmute();
        sender.sendMessage("§aJogador desmutado.");
        return false;
    }
}
