package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.StringUtils;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ReportCommand extends CommandBase {

    public ReportCommand() {
        super("report", "", "", Arrays.asList("reportar", "rp"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (args.length < 2){
            sender.sendMessage("§cUsage: /" + lb + " [player] [reason]");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            sender.sendMessage("§cAlvo não encontrado.");
            return false;
        }
        Gamer gamer = Gamer.getGamer(player.getUniqueId());
        if (gamer.isMuted()){
            player.sendMessage("§cVocê está silenciado.");
            return false;
        }
        String reason = StringUtils.createArgs(1, args, "", false);
        Main.getInstance().getGamerProvider().getGamers().stream().filter(g -> g.getGroup().ordinal() <= Group.YOUTUBER_PLUS.ordinal()).forEach(g -> {
            g.sendMessage("");
            g.sendMessage("§e§lREPORT");
            g.sendMessage("§fVítima: §a" + sender.getName());
            g.sendMessage("§fAcusado: §a" + player.getName());
            g.sendMessage("§fMotivo: §e" + reason);
            g.sendMessage("");
        });
        return true;
    }
}
