package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.LongManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GroupSetCommand extends CommandBase {

    public GroupSetCommand() {
        super("groupset", "", "", Arrays.asList("setgroup", "setgrupo"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.ADMIN))
            return false;

        if (args.length != 2 && args.length != 3) {
            sender.sendMessage("§cUsage: /" + lb + " [player] [group] [time]");
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage("§cJogador não encontrado.");
            return false;
        }
        Gamer gamer;
        if (player.isOnline())
            gamer = Gamer.getGamer(player.getUniqueId());
        else
            gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());

        Group group = Group.getGroupByName(args[1]);

        if (sender instanceof Player) {
            Gamer senderGamer = Gamer.getGamer(((Player) sender).getUniqueId());

            if (gamer.getGroup().ordinal() <= senderGamer.getGroup().ordinal()) {
                sender.sendMessage("§cVocê não pode gerenciar alguém com um cargo igual ou superior ao seu.");
                return false;
            }
            if (group != null && group.ordinal() <= senderGamer.getGroup().ordinal()){
                sender.sendMessage("§cEsse cargo é igual ou superior ao seu.");
                return false;
            }
        }

        if (group == null) {
            sender.sendMessage("§cGrupo inexistente.");
            return false;
        }
        long time = -1;

        if (args.length == 3)
            time = LongManager.stringToLong(args[2]);

        gamer.setGroup(group, time);

        if (!player.isOnline())
            Main.getInstance().getGamerProvider().update(gamer);

        sender.sendMessage("§aGrupo setado com sucesso.");
        return false;
    }
}
