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

public class LoginCommand extends CommandBase {

    public LoginCommand() {
        super("login", "", "", Collections.singletonList("logar"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (isPlayer(sender)) {
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

            if (gamer.getPassword() == null) {
                sender.sendMessage("§cVocê não está registrado.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§cUsage: /" + lb + " [password]");
            } else {
                if (Util.decode(gamer.getPassword()).equals(args[0])) {
                    Main.getInstance().getStorage().removeNeedLogin(sender.getName());
                    sender.sendMessage("§aAutenticado com sucesso.");
                } else {
                    sender.sendMessage("§cSenha errada.");
                }
            }
        }
        return true;
    }
}
