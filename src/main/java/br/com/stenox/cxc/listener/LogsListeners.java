package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.logs.LogType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.atomic.AtomicReference;

public class LogsListeners implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND){
            Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());

            Main.getInstance().getGamerProvider().getGamers().stream().filter(p -> p.getGroup().ordinal() < gamer.getGroup().ordinal()).forEach(o -> {
                String teleported = getPlayerTeleported(event.getTo());
                o.sendLogMessage(LogType.TELEPORT, event.getPlayer().getName(), teleported);
            });
        }
    }

    private String getPlayerTeleported(Location location){
        AtomicReference<String> player = new AtomicReference<>();

        location.getWorld().getNearbyEntities(location, 1, 1, 1).stream().filter(e -> e instanceof Player).forEach(entity -> {
            if (entity.getLocation().equals(location))
                player.set(entity.getName());
        });

        return player.get();
    }
}
