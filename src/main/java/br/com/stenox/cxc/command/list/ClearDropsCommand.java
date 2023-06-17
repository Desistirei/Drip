package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearDropsCommand extends CommandBase {

    public ClearDropsCommand() {
        super("cleardrops");
    }

    private static boolean IN_CLEAR_DROPS;

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.YOUTUBER_PLUS)){
            return false;
        }

        if (IN_CLEAR_DROPS){
            sender.sendMessage("§cJá tem um clear drops em andamento.");
            return false;
        }

        IN_CLEAR_DROPS = true;

        new BukkitRunnable(){
            int i = 5;

            @Override
            public void run() {
                if (i == 0){
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()){
                            if (entity instanceof Item){
                                entity.remove();
                            }
                        }
                    }

                    Bukkit.broadcastMessage("§cItens do chão foram removidos.");

                    IN_CLEAR_DROPS = false;

                    cancel();
                    return;
                }

                Bukkit.broadcastMessage("§cLimpando os itens do chão em " + i + " segundo" + (i == 1 ? "" : "s") + ".");
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        return false;
    }
}
