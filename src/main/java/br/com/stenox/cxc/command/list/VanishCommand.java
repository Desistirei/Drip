package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.gamer.logs.LogType;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class VanishCommand extends CommandBase {

    public VanishCommand() {
        super("vanish", "", "", Collections.singletonList("v"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender)){
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length == 0) {

            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());

            gamer.setVanish(!gamer.isVanish());

            Main.getInstance().getGamerProvider().getGamers().stream().filter(p -> p.getGroup().ordinal() < gamer.getGroup().ordinal()).forEach(o -> {
                o.sendLogMessage(LogType.VANISH, gamer.getName(), null);
            });

            if (gamer.isVanish()) {
                gamer.setContents(player.getInventory().getContents());
                gamer.setArmorContents(player.getInventory().getArmorContents());

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                player.setGameMode(GameMode.CREATIVE);
                player.teleport(player.getLocation().add(0, 0.75, 0));
                player.setFlying(true);

                player.sendMessage("§dVocê entrou no modo VANISH.");
                player.sendMessage("§dAgora você está invisível para todos sem OP.");
            } else {
                player.getInventory().setContents(gamer.getContents());
                player.getInventory().setArmorContents(gamer.getArmorContents());

                gamer.setContents(null);
                gamer.setArmorContents(null);

                player.setGameMode(GameMode.SURVIVAL);

                player.sendMessage("§dVocê saiu do modo VANISH.");
                player.sendMessage("§dAgora você está visível para todos os jogadores.");
            }
        } else {
            if (args[0].equalsIgnoreCase("v")){
                Player player = (Player) sender;
                Gamer gamer = Gamer.getGamer(player.getUniqueId());

                gamer.setInvisible(!gamer.isInvisible());

                if (gamer.isInvisible())
                    player.sendMessage("§cVocê ficou invisível para todos.");
                else
                    player.sendMessage("§aVocê ficou visível para todos.");
            }
        }
        Main.getInstance().getGamerProvider().updatePlayers();
        return false;
    }
}
