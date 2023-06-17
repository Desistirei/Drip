package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForceKitCommand extends CommandBase {

    public ForceKitCommand() {
        super("forcekit");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /forcekit [player, all] [kit]");
            return false;
        }
        Kit kit = Main.getInstance().getKitManager().searchKit(args[1]);
        if (kit == null) {
            sender.sendMessage("§cKit não encontrado.");
            return false;
        }
        if (args[0].equalsIgnoreCase("all")) {
            for (Gamer gamer : Main.getInstance().getGamerProvider().getGamers()) {
                if (gamer.getPlayer() == null) continue;
                gamer.setKit(kit);

                if (Main.getInstance().getGame().getStage() != GameStage.STARTING)
                    kit.give(gamer.getPlayer());

                gamer.sendMessage("§aVocê recebeu o kit " + kit.getName() + ".");
            }
            sender.sendMessage("§aVocê forçou o kick " + kit.getName() + " para todos os jogadores.");
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cJogador não encontrado.");
                return false;
            }
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            gamer.setKit(kit);

            if (Main.getInstance().getGame().getStage() != GameStage.STARTING)
                kit.give(player);

            gamer.sendMessage("§aVocê recebeu o kit " + kit.getName() + ".");

            sender.sendMessage("§aVocê forçou o kit " + kit.getName() + " para " + player.getName() + ".");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1){
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();

            if (StringUtil.startsWithIgnoreCase("all", lastWord))
                list.add("all");

            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName().toLowerCase();

                if (StringUtil.startsWithIgnoreCase(name, lastWord))
                    list.add(name);
            }

            return list;
        } else if (args.length == 2) {
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();

            for (Kit kit : Main.getInstance().getKitManager().getKits()) {
                String name = kit.getName().toLowerCase();

                if (StringUtil.startsWithIgnoreCase(name, lastWord))
                    list.add(name);
            }
            return list;
        }
        return Collections.emptyList();
    }
}
