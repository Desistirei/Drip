package br.com.stenox.cxc.command.list.punishments;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class UnbanCommand extends CommandBase {

    public UnbanCommand() {
        super("unban");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD_PLUS))
            return false;
        if (args.length != 1){
            sender.sendMessage("§cUsage: /unban [player]");
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cJogador não encontrado.");
            return false;
        }

        Gamer gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());

        if (gamer == null){
            sender.sendMessage("§cGamer não encontrado.");
            return false;
        } else if (!gamer.isBanned()){
            sender.sendMessage("§cJogador não está banido.");
            return false;
        }
        gamer.unban();
        sender.sendMessage("§aJogador desbanido.");
        return false;
    }
}
