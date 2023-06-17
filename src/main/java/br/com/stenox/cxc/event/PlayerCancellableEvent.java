package br.com.stenox.cxc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCancellableEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers;
    private boolean cancelled;

    static {
        handlers = new HandlerList();
    }

    public PlayerCancellableEvent(final Player player) {
        super(player);
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return PlayerCancellableEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerCancellableEvent.handlers;
    }
}
