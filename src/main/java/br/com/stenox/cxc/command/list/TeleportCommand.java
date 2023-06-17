package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeleportCommand extends CommandBase {

    public TeleportCommand() {
        super("teleport", "", "", Arrays.asList("tp", "teleporte", "go"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender))
            return false;
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        Player player = (Player) sender;

        if (args.length == 3){
            if (isInteger(args[0]) && isInteger(args[1]) && isInteger(args[2])){
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);

                player.teleport(new Location(player.getWorld(), x, y, z));
                player.sendMessage("§aTeleportado.");
            }
        } else if (args.length == 2){
            Player player1 = Bukkit.getPlayer(args[0]);
            Player player2 = Bukkit.getPlayer(args[1]);
            if (player1 == null || player2 == null){
                player.sendMessage("§cJogador inexistente.");
                return false;
            }
            player1.teleport(player2);
            player.sendMessage("§aTeleportado.");
        } else if (args.length == 1){
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null){
                player.sendMessage("§cJogador inexistente.");
                return false;
            }
            player.teleport(target);
            player.sendMessage("§aTeleportado.");
        }
        return false;
    }
}
