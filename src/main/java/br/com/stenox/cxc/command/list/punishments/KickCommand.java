package br.com.stenox.cxc.command.list.punishments;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class KickCommand extends CommandBase {

    public KickCommand() {
        super("kick", "", "", Collections.singletonList("expulsar"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length < 1){
            sender.sendMessage("§cUsage: /" + lb + " [player] [message]");
            return false;
        }
        if (args.length == 1){
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage("§cJogador não encontrado.");
                return false;
            }
            player.setHealth(0.0D);
            player.kickPlayer("§cVocê foi expulso do servidor. \n Sem motivo \n Por: " + sender.getName());
            sender.sendMessage("§aVocê expulsou o jogador com sucesso.");
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage("§cJogador não encontrado.");
                return false;
            }
            String message = StringUtils.createArgs(1, args, "", false);
            player.kickPlayer("§cVocê foi expulso do servidor. \n Motivo: " + message + " \n Por: " + sender.getName());
            sender.sendMessage("§aVocê expulsou o jogador com sucesso.");
        }
        return false;
    }
}
