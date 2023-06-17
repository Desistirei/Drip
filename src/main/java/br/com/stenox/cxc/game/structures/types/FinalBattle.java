package br.com.stenox.cxc.game.structures.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FinalBattle
{
    public FinalBattle() {
        this.spawnPit();
        this.teleportPlayers();
        this.removeEntities();
    }
    
    private void spawnPit() {
        Location spawn = this.getSpawn();
        spawn.setY(6.0);
        int radius = 29;
        int bed = 30;
        while (spawn.getBlockY() <= spawn.getWorld().getMaxHeight()) {
            /*for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    Location l = new Location(spawn.getWorld(), x, spawn.getBlockY(), z);
                    if (spawn.distance(l) < radius) {
                        l.getBlock().setType(Material.AIR);
                    }
                }
            }*/
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    if (x == radius || x == -radius || z == radius || z == -radius) {
                        Location l = new Location(spawn.getWorld(), x, spawn.getBlockY() - 5.0, z);
                        l.getBlock().setType(Material.BEDROCK);
                    }
                }
            }
            for (int x = -bed; x <= bed; ++x) {
                for (int z = -bed; z <= bed; ++z) {
                    if (x == bed || x == -bed || z == bed || z == -bed) {
                        Location l = new Location(spawn.getWorld(), x, spawn.getBlockY() - 5.0, z);
                        l.getBlock().setType(Material.BEDROCK);
                    }
                }
            }
            spawn = spawn.add(0.0, 1.0, 0.0);
        }
    }
    
	private void teleportPlayers() {
        Random r = new Random();
        int radius = 21;
        List<Location> locations = new ArrayList<>();
        Location spawn = this.getSpawn();
        spawn.setY(11.0);
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                Location l = new Location(spawn.getWorld(), x, 11.0, z);
                if (spawn.distance(l) <= radius && spawn.distance(l) >= radius - 2) {
                    locations.add(l);
                }
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc;
            if (locations.size() > 0) {
                int nexR = r.nextInt(locations.size() + 1);
                if (nexR < locations.size()) {
                    loc = locations.get(nexR);
                }
                else {
                    loc = locations.get(0);
                }
            }
            else {
                loc = new Location(spawn.getWorld(), 0.0, Bukkit.getWorld("world").getHighestBlockYAt(0, 0), 0.0);
            }
            p.setFallDistance(-5.0f);
            p.teleport(loc);
        }
    }
    
    private void removeEntities() {
        for (Entity e : this.getSpawn().getWorld().getEntities()) {
            if (!(e instanceof Player)) {
                e.remove();
            }
        }
    }
    
    public Location getSpawn() {
        return Bukkit.getServer().getWorlds().get(0).getSpawnLocation().add(0.0, 5.0, 0.0);
    }
}
