package br.com.stenox.cxc.command.list.punishments;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends CommandBase {

    public BanCommand() {
        super("ban");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length < 2){
            sender.sendMessage("§cUsage: /ban [player] [reason]");
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cJogador não encontrado.");
            return false;
        }
        Gamer gamer;

        if (player.isOnline())
            gamer = Gamer.getGamer(player.getUniqueId());
        else
            gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());

        if (gamer == null){
            sender.sendMessage("§cGamer não encontrado.");
            return false;
        }

        Gamer senderGamer = Gamer.getGamer(((Player)sender).getUniqueId());

        if (gamer.getGroup().ordinal() <= senderGamer.getGroup().ordinal()){
            sender.sendMessage("§cVocê não pode banir alguém com um cargo igual ou superior ao seu.");
            return false;
        }

        String reason = StringUtils.createArgs(1, args, "", false);

        gamer.ban(reason, -1);

        sender.sendMessage("§cJogador banido com sucesso.");

        if (player.isOnline()) {
            player.getPlayer().setHealth(0.0D);
            Main.getInstance().getGamerRepository().update(gamer);
            Main.getInstance().getGamerProvider().remove(gamer.getUniqueId());
            player.getPlayer().kickPlayer("§cVocê foi banido do servidor \n \n §cMotivo: " + reason + " \n §cExpira em: Nunca");
            Bukkit.broadcastMessage("§cUm jogador na sua sala foi banido por uso de trapaças.");
        }
        return false;
    }
}
