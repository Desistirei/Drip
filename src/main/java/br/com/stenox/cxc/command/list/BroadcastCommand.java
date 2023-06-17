package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.utils.StringUtils;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BroadcastCommand extends CommandBase {

    public BroadcastCommand() {
        super("broadcast", "", "", Arrays.asList("bc", "aviso"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD))
            return false;

        if (args.length < 1){
            sender.sendMessage("§cUsage: /" + lb + " [mensagem]");
            return false;
        }
        String message = StringUtils.createArgs(0, args, "", true);
        Bukkit.broadcastMessage("§b§l" + Main.CLAN_NAME.toUpperCase() + "§8» §f" + message);
        return false;
    }
}
