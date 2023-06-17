package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends CommandBase {

    public PingCommand(){
        super("ping");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender))
            return false;
        Player player = (Player) sender;

        if (args.length == 0) {
            if (getPing(player) == 0) {
                sender.sendMessage("§aSeu ping é §a...ms");
            } else {
                sender.sendMessage("§aSeu ping é " + getPing(player) + "ms");
            }
        } else {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§cAlvo não encontrado.");
                return false;
            }
            if (!player.canSee(p)) {
                sender.sendMessage("§cAlvo não encontrado.");
                return false;
            }
            if (getPing(p) == 0) {
                sender.sendMessage("§aO ping de " + p.getName() + " ...ms");
            } else {
                sender.sendMessage("§aO ping de " + p.getName() + " é de " + getPing(p) + "ms");
            }
        }
        return false;
    }

    public int getPing(Player player){
        return ((CraftPlayer) player).getHandle().ping;
    }
}
