package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.utils.mojang.PlayerAPI;
import br.com.stenox.cxc.utils.mojang.disguise.SkinProvider;
import br.com.stenox.cxc.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand extends CommandBase {

    private Main plugin;

    public ResetCommand(Main plugin) {
        super("reset");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!(sender instanceof Player))
            return false;

        if (args.length != 1) {
            sender.sendMessage("§cUso de /" + lb + ":");
            sender.sendMessage("§c- /" + lb + " nick");
            return false;
        }

        if (args[0].equalsIgnoreCase("nick")) {
            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (gamer.getFake() != null) {
                gamer.setFake(null);
                PlayerAPI.changePlayerName(player, gamer.getName(), false);

                Main.getInstance().getTagProvider().setTag(player, gamer.getGroup().getTag());

                player.sendMessage("§aVocê resetou o seu nickname.");
                String[] properties = SkinProvider.applyDefaultSkinIfNotNull(gamer);
                if (properties == null) {
                    player.sendMessage("§cNão foi possível redefinir suas skin.");
                    return false;
                }

                gamer.setTag(gamer.getGroup().getTag());
            } else {
                player.sendMessage("§cVocê não está usando um nickname.");
            }
        } else {
            sender.sendMessage("§cUso de /" + lb + ":");
            sender.sendMessage("§c- /" + lb + " nick");
        }
        return false;
    }
}