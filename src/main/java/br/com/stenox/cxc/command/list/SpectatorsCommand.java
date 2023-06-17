package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SpectatorsCommand extends CommandBase {

    public SpectatorsCommand() {
        super("spectators", "", "", Collections.singletonList("specs"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender)){
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length != 1 || !(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))){
            sender.sendMessage("§cUsage: /" + lb + " [on, off]");
            return false;
        }

        Player player = (Player) sender;
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        if (args[0].equalsIgnoreCase("on")){
            if (gamer.isSpectators()){
                sender.sendMessage("§cVocê já está com os espectadores ativados.");
                return false;
            }
            gamer.setSpectators(true);
            gamer.updatePlayer();

            sender.sendMessage("§aAgora você está vendo os espectadores.");
        } else {
            if (!gamer.isSpectators()){
                sender.sendMessage("§cVocê já está com os espectadores desativados.");
                return false;
            }
            gamer.setSpectators(false);
            gamer.updatePlayer();

            sender.sendMessage("§cAgora você não está vendo os espectadores.");
        }
        return false;
    }
}
