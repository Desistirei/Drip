package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeleportAllCommand extends CommandBase {

    public TeleportAllCommand() {
        super("tpall");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;

        if (!isPlayer(sender)) {
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }

        Player player = (Player) sender;

        for (Player target : Bukkit.getOnlinePlayers())
            target.teleport(player.getLocation());

        Bukkit.broadcastMessage("§aTodos os jogadores foram teleportados.");

        return false;
    }
}
