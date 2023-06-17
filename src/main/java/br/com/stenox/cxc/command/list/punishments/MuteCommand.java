package br.com.stenox.cxc.command.list.punishments;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class MuteCommand extends CommandBase {

    public MuteCommand() {
        super("mute");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length < 2){
            sender.sendMessage("§cUsage: /mute [player] [reason]");
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cJogador não encontrado.");
            return false;
        }
        Gamer gamer;

        if (player.isOnline()){
            gamer = Gamer.getGamer(player.getUniqueId());
        } else {
            gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());
        }
        if (gamer == null){
            sender.sendMessage("§cGamer não encontrado.");
            return false;
        }
        String reason = StringUtils.createArgs(1, args, "", false);
        gamer.mute(reason, -1);

        sender.sendMessage("§aJogador mutado com sucesso.");

        if (player.isOnline())
            player.getPlayer().sendMessage("§cVocê foi mutado no servidor \n \n §cMotivo: " + reason + " \n §cExpira em: Nunca");
        return false;
    }
}
