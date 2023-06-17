package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.game.stage.GameStage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends CommandBase {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender))
            return false;
        Player player = (Player) sender;
        if (Main.getInstance().getGame().getStage() == GameStage.STARTING){
            player.teleport(Main.SPAWN);
        }
        return false;
    }
}
