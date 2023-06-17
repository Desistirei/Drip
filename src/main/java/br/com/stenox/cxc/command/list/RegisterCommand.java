package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.login.check.Check;
import br.com.stenox.cxc.login.exception.InvalidCheckException;
import br.com.stenox.cxc.login.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class RegisterCommand extends CommandBase {

    public RegisterCommand() {
        super("register", "", "", Collections.singletonList("registrar"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            try {
                if (Check.fastCheck(player.getName())) {
                    sender.sendMessage("§aVocê já está autenticado.");
                    return true;
                }
            } catch (InvalidCheckException e) {
                e.printStackTrace();
            }

            Gamer gamer = Gamer.getGamer(player.getUniqueId());

            if (gamer.getPassword() != null) {
                sender.sendMessage("§cVocê já está registrado.");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage("§cUsage: /" + lb + " [senha] [senha]");
            } else {
                if (!args[0].equals(args[1])) {
                    sender.sendMessage("§cAs senhas precisam ser idênticas.");
                    return true;
                }
                gamer.setPassword(Util.encode(args[0]));
                Main.getInstance().getStorage().removeNeedLogin(player.getName());
                sender.sendMessage("§aRegistrado com sucesso.");
                return true;
            }
        }
        return false;
    }
}
