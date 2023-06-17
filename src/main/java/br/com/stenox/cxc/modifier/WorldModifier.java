package br.com.stenox.cxc.modifier;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class WorldModifier {

    public void finalBattle(Location location, int size, int height, Material mat) {
        int clearSize = size + 2;
        int clearHeight = height + 2;
        for (int x = -clearSize; x <= clearSize + 7; x++) {
            for (int z = -clearSize; z <= clearSize + 7; z++) {
                for (int y = 0; y <= clearHeight; y++) {
                    int xx = x, yy = y, zz = z;
                    if (location.clone().add(xx, yy, zz).getBlock().getLocation().getBlock()
                            .getType() == Material.AIR)
                        continue;
                    if (location.clone().add(xx, yy, zz).getBlock().getLocation().getBlock() == null)
                        continue;
                    if (!location.clone().add(xx, yy, zz).getChunk().isLoaded())
                        continue;
                    setBlockFast(location.clone().add(xx, yy, zz).getBlock().getLocation(), Material.AIR, (byte) 0);
                }
            }
        }

        for (int y = 0; y <= height; y++) {
            if (y == 0) {
                for (int x = -size; x <= size; x++) {
                    for (int z = -size; z <= size; z++) {
                        Location loc = location.clone().add(x, y, z);
                        setBlockFast(location.getWorld(),
                                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), mat.getId(), (byte) 0);
                    }
                }
            } else {
                for (int x = -size; x <= size; x++) {
                    Location loc = location.clone().add(x, y, size);
                    Location loc2 = location.clone().add(x, y, -size);
                    setBlockFast(location.getWorld(), loc.getBlockX(),
                            loc.getBlockY(), loc.getBlockZ(), mat.getId(), (byte) 0);
                    setBlockFast(location.getWorld(), loc2.getBlockX(),
                            loc2.getBlockY(), loc2.getBlockZ(), mat.getId(), (byte) 0);
                }
                for (int z = -size; z <= size; z++) {
                    Location loc = location.clone().add(size, y, z);
                    Location loc2 = location.clone().add(-size, y, z);
                    setBlockFast(location.getWorld(), loc.getBlockX(),
                            loc.getBlockY(), loc.getBlockZ(), mat.getId(), (byte) 0);
                    setBlockFast(location.getWorld(), loc2.getBlockX(),
                            loc2.getBlockY(), loc2.getBlockZ(), mat.getId(), (byte) 0);
                }
            }
        }
    }

    public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
        if (y >= 255 || y < 0) {
            return false;
        }
        setAsyncBlock(world, x, y, z, blockId, data);
        net.minecraft.server.v1_8_R3.World world2 = ((CraftWorld) world).getHandle();
        world2.notify(new BlockPosition(x, y, z));
        return true;
    }

    public boolean setBlockFast(Location location, Material material, byte data) {
        return setBlockFast(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                material.getId(), data);
    }

    public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int i = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
        chunk.a(bp, ibd);
    }

    public void setAsyncBlockLocation(World world, Location ovo, Material material, byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(doubleToInt(ovo.getX()), doubleToInt(ovo.getZ()));
        BlockPosition bp = new BlockPosition(ovo.getBlockX(), ovo.getBlockY(), ovo.getBlockZ());
        int i = material.getId() + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
        chunk.a(bp, ibd);
    }

    public int doubleToInt(Double d) {
        return d.intValue();
    }
}
