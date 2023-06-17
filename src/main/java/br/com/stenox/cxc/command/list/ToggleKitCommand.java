package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ToggleKitCommand extends CommandBase {

    public ToggleKitCommand() {
        super("togglekit");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS))
            return false;
        if (args.length != 1){
            sender.sendMessage("§cUsage: /togglekit [kit, all]");
            return false;
        }
        if (args[0].equalsIgnoreCase("all")){
            if (Main.TOGGLE_KITS){
                Main.TOGGLE_KITS = false;
                for (Kit kit : Main.getInstance().getKitManager().getKits())
                    kit.setActive(false);

                for (Gamer gamer : Main.getInstance().getGamerProvider().getGamers())
                    gamer.setKit(null);

                Bukkit.broadcastMessage("§cTodos os kits foram desativados.");
            } else {
                Main.TOGGLE_KITS = true;
                for (Kit kit : Main.getInstance().getKitManager().getKits()) {
                    kit.setActive(true);
                }
                Bukkit.broadcastMessage("§aTodos os kits foram ativados.");
            }
        } else {
            String kitName = args[0];
            Kit kit = null;
            for (Kit k : Main.getInstance().getKitManager().getKits()) {
                if (k.getName().equalsIgnoreCase(kitName)) {
                    kit = k;
                    break;
                }
            }
            if (kit == null){
                sender.sendMessage("§cEste kit não foi encontrado.");
                return false;
            }
            kit.setActive(!kit.isActive());
            if (!kit.isActive()){
                for (Gamer gamer : Main.getInstance().getGamerProvider().getGamers()) {
                    if (gamer.getKit() != null && gamer.getKit().equals(kit)){
                        gamer.setKit(null);
                    }
                }
            }
        
            Bukkit.broadcastMessage(kit.isActive() ? "§aO kit " + kit.getName() + " foi ativado." : "§cO kit " + kit.getName() + " foi desativado.");
        }
        return false;
    }
}
