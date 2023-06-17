package br.com.stenox.cxc.game.structures.types;

import br.com.stenox.cxc.game.structures.Structure;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.bo3.reader.BO3Reader;
import br.com.stenox.cxc.game.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class MiniFeast extends Structure {

    private int x, z, y, size;
    private ArrayList<Block> chestsData;
    private ItemStack[] feastStacks = {new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.WATER_BUCKET),
            new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.ENDER_PEARL, getRandom(3)),
            new ItemStack(Material.POTION, 1, (short) 16421), new ItemStack(Material.WEB, getRandom(4)),
            new ItemStack(Material.POTION, 1, (short) 16426), new ItemStack(Material.POTION, 1, (short) 8225),
            new ItemStack(Material.IRON_INGOT, getRandom(2))};

    public MiniFeast(GameManager gameManager, int size) {
        super(gameManager);
        this.size = size;
        this.chestsData = new ArrayList<>();

        x = getCoord(150);
        z = getCoord(150);

        this.location = getManager().getWorld().getHighestBlockAt(x, z).getLocation();

        y = getManager().getWorld().getHighestBlockAt(x, z).getLocation().getBlockY();

        if (!location.getWorld().isChunkLoaded(location.getBlockX(), location.getBlockZ())) {
            location.getWorld().loadChunk(location.getBlockX(), location.getBlockZ());
        }

        createStructure();
        Location loc1, loc2, loc3, loc4;

        loc1 = new Location(getManager().getWorld(), x + 1, y + 3, z + 1);
        loc1.getBlock().setType(Material.CHEST);
        chestsData.add(loc1.getBlock());

        loc2 = new Location(getManager().getWorld(), x + 1, y + 3, z + -1);
        loc2.getBlock().setType(Material.CHEST);
        chestsData.add(loc2.getBlock());

        loc3 = new Location(getManager().getWorld(), x - 1, y + 3, z + 1);
        loc3.getBlock().setType(Material.CHEST);
        chestsData.add(loc3.getBlock());

        loc4 = new Location(getManager().getWorld(), x - 1, y + 3, z + -1);
        loc4.getBlock().setType(Material.CHEST);
        chestsData.add(loc4.getBlock());

        for (Block chests : chestsData) {
            if (chests.getType() == Material.CHEST) {
                chests.setType(Material.CHEST);
                Chest chest = (Chest) chests.getLocation().getBlock().getState();
                addChestItems(chest, feastStacks);
                chest.update();
            }
        }

    }

    public Location getLocation() {
        return location;
    }

    public void spawnMiniFeast() {
        if (getManager().getWorld() != Bukkit.getWorld("world")) {
            return;
        }
        forceMiniFeast();
    }

    public void forceMiniFeast() {
        if (getManager().getWorld() != Bukkit.getWorld("world")) {
            return;
        }

        Bukkit.broadcastMessage("§cUm mini-feast spawnou entre (X: " + (x - 50) + " Z: " + getInteger(z) + ") e (X: " + getInteger(x) + " Z: " + (z - 50) + ").");
        spawnChests();
    }

    public int getInteger(int i) {
        int ad = new Random().nextBoolean() ? -50 : 50;
        return ad + i;
    }

    public void createStructure() {
        createArea();
        BO3Reader a = new BO3Reader(Main.getInstance().getMiniFeastFile()) {
            @Override
            public void onSpawn(Block block) {
                if (block.getType() == Material.CHEST) {
                    addChestItems((Chest) block.getLocation().getBlock().getState(), feastStacks);
                }
            }
        };
        a.spawn(this.location);
    }

    public void spawnChests() {
        ((World) getManager().getWorld()).strikeLightningEffect(location.clone().add(0, 1, 0));
    }

    private void createArea() {
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                if (location.clone().add(x, 0, z).distance(location) > size + 10.0D)
                    continue;
                for (int y = 0; y <= 5; y++) {
                    getManager().getWorldModifier().setBlockFast(location.clone().add(x, y, z).getBlock().getLocation(), Material.AIR, (byte) 0);
                }
            }
        }
    }

    private int getRandom(int i) {
        return Math.max(1, new Random().nextInt(i + 1));
    }

    private int getCoord(int range) {
        int cord = new Random().nextInt(range) + 200;
        cord = (new Random().nextBoolean() ? -cord : cord);

        return cord;
    }

    public void addChestItems(final Chest chest, final ItemStack[] stacks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < stacks.length; i++)
                    if (new Random().nextInt(100) < 31)
                        chest.getInventory().setItem(new Random().nextInt(27), stacks[i]);
                if (new Random().nextInt(100) < 4) {
                    chest.getInventory().setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND));
                }
                if (new Random().nextInt(100) <= 3) {
                    chest.getInventory().setItem(new Random().nextInt(27), new ItemStack(Material.IRON_HELMET));
                }
                if (new Random().nextInt(110) < 5) {
                    chest.getInventory().setItem(new Random().nextInt(27), new ItemStack(Material.MILK_BUCKET));
                }
                chest.update();
            }
        }.runTaskLater(Main.getInstance(), 30L);
    }
}
