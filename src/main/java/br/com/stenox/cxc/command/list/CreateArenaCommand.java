package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArenaCommand extends CommandBase {

    public CreateArenaCommand() {
        super("createarena");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length != 2){
            sender.sendMessage("§cUsage: /createarena [x] [y]");
            return false;
        }
        if (!isInteger(args[0]) || !isInteger(args[1])){
            sender.sendMessage("§cNúmero inválido.");
            return false;
        }
        int numberX = Integer.parseInt(args[0]);
        int numberY = Integer.parseInt(args[1]);

        Player player = (Player) sender;

        for (int x = -numberX; x < numberX; x++){
            for (int z = -numberX; z < numberX; z++){
                for (int y = -numberY; y < numberY; y++){
                    Location location = player.getLocation().clone().add(x, y, z);
                    location.getBlock().setType(Material.BEDROCK);
                }
            }
        }

        numberX = numberX - 1;
        numberY = numberY - 1;

        for (int x = -numberX; x < numberX; x++){
            for (int z = -numberX; z < numberX; z++){
                for (int y = -numberY; y < numberY; y++){
                    Location location = player.getLocation().clone().add(x, y, z);
                    location.getBlock().setType(Material.AIR);
                }
            }
        }
        return false;
    }
}
