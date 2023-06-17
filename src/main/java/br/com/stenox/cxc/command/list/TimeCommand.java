package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TimeCommand extends CommandBase {

    public TimeCommand() {
        super("tempo");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length > 1){
            sender.sendMessage("§cUsage: /" + lb + " [time]");
            return false;
        }

        int time = Integer.parseInt(args[1]);

        Main.getInstance().getGame().setTime(time);
        Bukkit.broadcastMessage("§aTempo alterado para " + Formatter.toTimeLong(time));
        return false;
    }
}
