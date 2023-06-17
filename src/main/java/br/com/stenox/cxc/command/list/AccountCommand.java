package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.utils.LongManager;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class AccountCommand extends CommandBase {

    public AccountCommand() {
        super("account", "", "", Collections.singletonList("acc"));
    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (args.length == 0) {
                sender.sendMessage("§aUsuário: §f" + gamer.getName());
                sender.sendMessage("§aRank: §f" + gamer.getGroup().getName());
                if (gamer.getGroupTime() != -1)
                    sender.sendMessage(" §7Expira em " + LongManager.formatLong(gamer.getGroupTime()));
                if (gamer.isBanned() || gamer.isMuted()){
                    sender.sendMessage("§cPunições ativas:");
                    if (gamer.isBanned())
                        sender.sendMessage("§cBanido por " + gamer.getBanReason() + " Expira em " + LongManager.formatLong(gamer.getBanTime()));
                    if (gamer.isMuted())
                        sender.sendMessage("§cMutado por " + gamer.getMuteReason() + " Expira em " + LongManager.formatLong(gamer.getMuteTime()));
                }
            } else {
                if (hasPermission(sender, Group.ADMIN)) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (offlinePlayer == null) {
                        player.sendMessage("§cJogador não encontrado.");
                        return false;
                    }

                    Gamer target = Main.getInstance().getGamerProvider().search(offlinePlayer.getName());
                    if (target == null)
                        target = Main.getInstance().getGamerRepository().fetch(offlinePlayer.getUniqueId());

                    sender.sendMessage("§aUsuário: §f" + target.getName());
                    sender.sendMessage("§aRank: §f" + target.getGroup().getName());
                    if (gamer.getGroupTime() != -1)
                        sender.sendMessage(" §7Expira em " + LongManager.formatLong(target.getGroupTime()));
                    sender.sendMessage("§aIp: §f" + target.getIp());
                    if (target.isBanned() || target.isMuted()) {
                        sender.sendMessage("§cPunições ativas:");
                        if (target.isBanned())
                            sender.sendMessage("§cBanido por " + target.getBanReason() + " Expira em " + LongManager.formatLong(target.getBanTime()));
                        if (target.isMuted())
                            sender.sendMessage("§cMutado por " + target.getMuteReason() + " Expira em " + LongManager.formatLong(target.getMuteTime()));
                    }
                }
            }
        }
        return false;
    }
}
