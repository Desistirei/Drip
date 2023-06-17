package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class GamemodeCommand extends CommandBase {

    public GamemodeCommand() {
        super("gamemode", "", "", Collections.singletonList("gm"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender)){
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (args.length == 1) {
            GameMode mode = getGameMode(args[0]);
            if (mode == null) {
                sender.sendMessage("§cModo não encontrado.");
                return false;
            }
            ((Player) sender).setGameMode(mode);
            sender.sendMessage("§aModo de jogo atualizado para §f" + mode.name());
        } else if (args.length == 2){
            GameMode mode = getGameMode(args[0]);
            if (mode == null) {
                sender.sendMessage("§cModo não encontrado.");
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null){
                sender.sendMessage("§cJogador inexistente.");
                return false;
            }
            player.setGameMode(mode);
            sender.sendMessage("§aModo de jogo de " + player.getName() + " atualizado para §f" + mode.name());
        } else {
            sender.sendMessage("§cUsage: /" + lb + " [mode]");
            return false;
        }
        return false;
    }

    private GameMode getGameMode(String s){
        for (GameMode mode : GameMode.values()) {
            if (isInteger(s)){
                if (mode.getValue() == Integer.parseInt(s)){
                    return mode;
                }
            } else {
                if (mode.name().equalsIgnoreCase(s)){
                    return mode;
                }
            }
        }
        return null;
    }
}
