package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class LockCommand extends CommandBase {

    public LockCommand() {
        super("lock", "", "", Collections.singletonList("bloquear"));
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD)){
            return false;
        }
        Main.LOCK = !Main.LOCK;
        Bukkit.broadcastMessage(Main.LOCK ? "§cO Modo Recuperação foi ativado." : "§cO Modo Recuperação foi desativado.");
        return false;
    }
}