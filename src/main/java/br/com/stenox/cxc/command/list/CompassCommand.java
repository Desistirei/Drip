package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class CompassCommand extends CommandBase {

    public CompassCommand() {
        super("compass", "", "", Collections.singletonList("bussola"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!isPlayer(sender))
            return false;
        Player player = (Player) sender;
        player.getInventory().addItem(new ItemStack(Material.COMPASS));
        return false;
    }
}
