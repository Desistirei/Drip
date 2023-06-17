package br.com.stenox.cxc.game.structures;

import br.com.stenox.cxc.game.manager.GameManager;
import org.bukkit.Location;

public abstract class Structure {

    private final GameManager gameManager;

    protected Location location;

    public Structure(GameManager gameManager) {
        this.gameManager = gameManager;

    }

    public Structure(GameManager gameManager, Location location) {
        this.gameManager = gameManager;
        this.location = location;

    }

    public GameManager getManager() {
        return gameManager;
    }

    public Location getLocation() {
        return location;
    }
}
