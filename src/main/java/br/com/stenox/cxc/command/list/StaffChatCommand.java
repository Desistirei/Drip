package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StaffChatCommand extends CommandBase {

    public StaffChatCommand() {
        super("staffchat", "", "", Collections.singletonList("sc"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender))
            return false;
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        Player player = (Player) sender;
        Gamer gamer = Gamer.getGamer(player.getUniqueId());
        if (!gamer.isStaffChat()){
            gamer.setStaffChat(true);
            gamer.setStaffMessages(true);

            player.sendMessage("§aVocê entrou no StaffChat.");
        } else {
            gamer.setStaffChat(false);
            gamer.setStaffMessages(false);

            player.sendMessage("§cVocê saiu no StaffChat.");
        }
        return false;
    }
}
