package br.com.stenox.cxc.command;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CommandBase extends Command {

    public CommandBase(String name) {
        super(name);
    }

    public CommandBase(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        return false;
    }

    public boolean isPlayer(CommandSender sender){
        return sender instanceof Player;
    }

    public boolean hasPermission(CommandSender sender, Group group){
        if (sender instanceof Player){
            Gamer gamer = Gamer.getGamer(((Player) sender).getUniqueId());
            if (gamer.getGroup().ordinal() <= group.ordinal() || sender.isOp())
                return true;
            else {
                sendNoPermissionMessage(sender);
                return false;
            }
        } else {
            if (sender.isOp())
                return true;
            else {
                sendNoPermissionMessage(sender);
                return false;
            }
        }
    }

    public void sendNoPermissionMessage(CommandSender sender){
        sender.sendMessage("§cVocê não tem permissão para executar este comando.");
    }

    public Main getPlugin() {
        return Main.getInstance();
    }

    public boolean isInteger(String text){
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ignored){
            return false;
        }
    }
}
