package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class TellCommand extends CommandBase {

    public TellCommand() {
        super("tell", "", "", Arrays.asList("whisper", "w"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage("§cUsage: /" + lb + " <player> <message>");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (!gamer.isTell()) {
                        gamer.setTell(true);

                        player.sendMessage("§aVocê habilitou suas mensagens privadas.");
                    } else {
                        player.sendMessage("§cSuas mensagens privadas já estão habilitadas.");
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (gamer.isTell()) {
                        gamer.setTell(false);
                        player.sendMessage("§cVocê desabilitou suas mensagens privadas.");
                    } else {
                        player.sendMessage("§cSuas mensagens privadas já estão desabilitadas.");
                    }
                } else {
                    player.sendMessage("§cUsage: /" + lb + " <player> <message>");
                }
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (target.getUniqueId() != player.getUniqueId()) {
                        Gamer targetGamer = Gamer.getGamer(target.getUniqueId());
                        if (targetGamer.getGroup().ordinal() > Group.YOUTUBER_PLUS.ordinal()) {
                            if (!player.canSee(target) && !isEnvolved(player.getUniqueId(), target.getUniqueId())) {
                                player.sendMessage("§cAlvo não encontrado.");
                                return false;
                            }
                        }


                        if (!targetGamer.isTell()) {
                            player.sendMessage("§cO alvo está com as mensagens privadas desativadas ou ele te bloqueou.");
                            return false;
                        }

                        if (gamer.isMuted()){
                            player.sendMessage("§cVocê está mutado " + (gamer.getMuteTime() == -1 ? "permanentemente" : "temporariamente") + ".");
                            return false;
                        }

                        String message = StringUtils.createArgs(1, args, "", false);

                        targetGamer.setLastTell(player.getUniqueId());
                        gamer.setLastTell(target.getUniqueId());

                        target.sendMessage("§8[§7" + player.getName() + " §8§l> §7Você§8] §e" + message);
                        player.sendMessage("§8[§7Você §8§l> §7" + target.getName() + "§8] §e" + message);
                    } else {
                        player.sendMessage("§cVocê não pode conversar consigo mesmo.");
                    }
                } else {
                    player.sendMessage("§cAlvo não encontrado.");
                }
            }
        }
        return false;
    }

    public boolean isEnvolved(UUID p1, UUID p2) {
        Gamer pp1 = Gamer.getGamer(p1);
        Gamer pp2 = Gamer.getGamer(p2);
        if (pp1.getLastTell() != null && pp1.getLastTell().toString().equalsIgnoreCase(p2.toString()))
            return true;
        else if (pp2.getLastTell() != null && pp2.getLastTell().toString().equalsIgnoreCase(p1.toString()))
            return true;

        return false;
    }
}
