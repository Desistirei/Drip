package br.com.stenox.cxc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.WorldServer;

public class ParticleAPI {

    public static void spawnParticle(EnumParticle part, double x, double y, double z) {
        spawnParticle(part, new Location(Bukkit.getWorlds().get(0), x, y, z));
    }

    public static void spawnParticle(EnumParticle part, Location location) {
        spawnParticle(part, location, 1, 0);
    }

    public static void spawnParticle(EnumParticle part, Location location, int quantity, double speed) {
        spawnParticle(part, location, 0, 0, 0, quantity, speed);
    }

    public static void spawnParticle(EnumParticle part, Location location, double ofX, double ofY, double ofZ, int quantidade, double speed) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            WorldServer world = ((CraftWorld) cp.getWorld()).getHandle();
            world.sendParticles(ep, part, true, location.getX(), location.getY(), location.getZ(), quantidade, ofX, ofY, ofZ, speed);
        });
    }

    public static void spawnParticle(Player p, EnumParticle part, Location location, double ofX, double ofY, double ofZ, int quantidade, double speed) {
        CraftPlayer cp = (CraftPlayer) p;
        EntityPlayer ep = cp.getHandle();
        WorldServer world = ((CraftWorld) cp.getWorld()).getHandle();
        world.sendParticles(ep, part, true, location.getX(), location.getY(), location.getZ(), quantidade, ofX, ofY, ofZ, speed);
    }

    public static void spawnParticlePacket(Player p, EnumParticle part, Location location) {
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(part, true, x, y, z, 0, 0, 0, 0, 1);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }
}