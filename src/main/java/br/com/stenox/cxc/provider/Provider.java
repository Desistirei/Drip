package br.com.stenox.cxc.provider;

import br.com.stenox.cxc.Main;
import org.bukkit.Server;
import org.bukkit.event.Listener;

public abstract class Provider {

    private Main plugin;

    public Provider(final Main plugin) {
        this.plugin = plugin;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public Main getPlugin() {
        return plugin;
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public void registerListener(final Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
