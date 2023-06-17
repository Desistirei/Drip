package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeastCommand extends CommandBase {

    public FeastCommand() {
        super("feast");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (Main.getInstance().getGame().getTimer().getFeast() == null) {
            sender.sendMessage("§cOps! O feast ainda não foi spawnado.");
            return false;
        }
        if (!Gamer.getGamer(player.getUniqueId()).isAlive()) {
            player.teleport(Main.getInstance().getGame().getTimer().getFeast().getLocation().clone().add(0, 1.2, 0));
            player.sendMessage("§aTeleportado!");
        } else {
            player.setCompassTarget(Main.getInstance().getGame().getTimer().getFeast().getLocation());
            player.sendMessage("§aSua bússola agora está apontando para o feast.");
        }
        return false;
    }
}
