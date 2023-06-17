package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ChatCommand extends CommandBase {

    public ChatCommand() {
        super("chat", "", "", Collections.singletonList("cc"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (lb.equalsIgnoreCase("cc")){
            for (int i = 0; i < 100; i++)
                Bukkit.broadcastMessage("");
            return true;
        } else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")){
                    if (Main.CHAT){
                        sender.sendMessage("§cO chat já está ativado.");
                        return false;
                    }
                    Main.CHAT = true;
                    Bukkit.broadcastMessage("§aO chat foi ativado.");
                    return true;
                } else if (args[0].equalsIgnoreCase("off")){
                    if (!Main.CHAT){
                        sender.sendMessage("§cO chat já está desativado.");
                        return false;
                    }
                    Main.CHAT = false;
                    Bukkit.broadcastMessage("§cO chat foi desativado.");
                    return true;
                } else if (args[0].equalsIgnoreCase("clear")){
                    for (int i = 0; i < 100; i++)
                        Bukkit.broadcastMessage("");
                    return true;
                }
            }
        }
        sender.sendMessage("§cUsage: /chat [on, off, clear]");
        return false;
    }
}
