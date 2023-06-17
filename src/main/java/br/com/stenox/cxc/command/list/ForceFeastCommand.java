package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.game.structures.types.Feast;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.command.CommandSender;

public class ForceFeastCommand extends CommandBase {

    public ForceFeastCommand() {
        super("forcefeast");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (Main.getInstance().getGame().getStage() != GameStage.IN_GAME){
            sender.sendMessage("§cO partida precisa estar em andamento.");
            return false;
        }

        Feast feast = Main.getInstance().getGame().getTimer().getFeast();
        if (feast != null){
            if (Feast.feastTime > 0){
                Feast.feastTime = 5;
            } else {
                sender.sendMessage("§cO feast já spawnou.");
            }
        } else {
            feast = new Feast(Main.getInstance().getGameManager(), 15);
            feast.spawnFeast();

            Main.getInstance().getGame().getTimer().setFeast(feast);
        }
        return false;
    }
}