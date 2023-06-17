package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.kit.special.SpecialKit;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecialKitCommand extends CommandBase {

    public SpecialKitCommand(){
        super("skit");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender)) {
            sender.sendMessage("§cEste comando é apenas para jogadores.");
            return false;
        }
        if (!hasPermission(sender, Group.YOUTUBER_PLUS)){
            return false;
        }
        Player player = (Player) sender;
        if (args.length <= 1) {
            if (args.length != 1) {
                sender.sendMessage("§cUso do /skit:");
                sender.sendMessage("§c- /skit new <name>");
                sender.sendMessage("§c- /skit apply <name> <player/all>");
                sender.sendMessage("§c- /skit delete <name>");
                sender.sendMessage("§c- /skit list");
            } else if (args[0].equalsIgnoreCase("list")) {
                if (SpecialKit.specialKits.size() == 0) {
                    sender.sendMessage("§cNenhum inventário foi salvo ainda.");
                    return false;
                }
                for (SpecialKit kit : SpecialKit.specialKits)
                    player.sendMessage("§b" + kit.getName() + " §e- §6" + kit.getCreator());
            } else {
                sender.sendMessage("§cUso do /skit:");
                sender.sendMessage("§c- /skit new <name>");
                sender.sendMessage("§c- /skit apply <name> <player/all>");
                sender.sendMessage("§c- /skit delete <name>");
                sender.sendMessage("§c- /skit list");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                if (SpecialKit.getKit(args[1]) != null) {

                    SpecialKit kit = SpecialKit.getKit(args[1]);
                    SpecialKit.specialKits.remove(kit);

                    assert kit != null;
                    sender.sendMessage("§cVocê apagou o kit " + kit.getName() + " com sucesso!");
                    return false;
                } else {
                    sender.sendMessage("§cInventário não encontrado.");
                }
            }
            if (args[0].equalsIgnoreCase("new")) {
                if (SpecialKit.getKit(args[1]) == null) {
                    SpecialKit k = new SpecialKit(args[1], player.getInventory().getContents(), player.getInventory().getArmorContents(), sender.getName());
                    SpecialKit.createKit(k);

                    sender.sendMessage("§aSeu inventário foi salvo como " + k.getName());
                    if (player.getInventory().firstEmpty() == -1) {
                        sender.sendMessage("§cSeu inventário está cheio, os items dos kits não serão devidamente adicionado ao inventário.");
                    }
                } else {
                    sender.sendMessage("§cUm inventário já está salvo com este nome.");
                }
            } else if (args[0].equalsIgnoreCase("apply")) {
                sender.sendMessage("§cUso correto: /skit apply <name> <player/all>");
            } else {
                sender.sendMessage("§cUso do /skit:");
                sender.sendMessage("§c- /skit new <name>");
                sender.sendMessage("§c- /skit apply <name> <player/all>");
                sender.sendMessage("§c- /skit delete <name>");
                sender.sendMessage("§c- /skit list");
            }
        } else if (args[0].equalsIgnoreCase("apply")) {
            SpecialKit k2 = SpecialKit.getKit(args[1]);
            if (k2 != null) {
                if (args[2].equalsIgnoreCase("all")) {
                    for (Gamer gamer : Main.getInstance().getGameManager().getAlivePlayers()) {
                        if (gamer == null || gamer.getPlayer() == null) continue;
                        Player find = gamer.getPlayer();
                        find.getInventory().clear();
                        find.closeInventory();
                        find.getInventory().setContents(k2.getContents());
                        find.getInventory().setArmorContents(k2.getArmorContents());
                        if (gamer.getKit() != null) gamer.getKit().give(find);
                        find.updateInventory();
                    }
                    sender.sendMessage("§aVocê aplicou o inventário §a" + k2.getName() + "§a para todos os jogadores.");
                } else {
                    Player p = Bukkit.getPlayer(args[2]);
                    if (p == null) {
                        player.sendMessage("§cAlvo não encontrado.");
                        return false;
                    }
                    p.closeInventory();
                    p.getInventory().clear();
                    p.getInventory().setContents(k2.getContents());
                    p.getInventory().setArmorContents(k2.getArmorContents());
                    p.updateInventory();

                    sender.sendMessage("§aVocê aplicou o inventário §a" + k2.getName() + "§a para o jogador " + p.getName() + ".");
                }
            } else {
                sender.sendMessage("§cNão foi encontrado nenhum inventário com este nome.");
                return false;
            }

        } else {
            sender.sendMessage("§cUso do /skit:");
            sender.sendMessage("§c- /skit new <name>");
            sender.sendMessage("§c- /skit apply <name> <player/all>");
            sender.sendMessage("§c- /skit delete <name>");
            sender.sendMessage("§c- /skit list");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (isPlayer(sender)) {
            if (args.length == 2 && args[0].equalsIgnoreCase("apply")) {
                List<String> list = new ArrayList<>();
                for (SpecialKit specialKit : SpecialKit.specialKits) {
                    list.add(specialKit.getName());
                }
                return list;
            }
        }
        return Collections.emptyList();
    }
}