package br.com.stenox.cxc.game.structures.types;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.structures.Structure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class BonusFeast extends Structure {

    private ArrayList<Block> chestsData;
    private ArrayList<Location> blockData;

    private ItemStack[] feastStacks = { new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_SWORD),
            new ItemStack(Material.COOKED_BEEF, getRandom(37)), new ItemStack(Material.FLINT_AND_STEEL),
            new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.ENDER_PEARL, getRandom(5)), new ItemStack(Material.GOLDEN_APPLE, getRandom(12)),
            new ItemStack(Material.EXP_BOTTLE, getRandom(12)), new ItemStack(Material.WEB, getRandom(9)),
            new ItemStack(Material.TNT, getRandom(32)), new ItemStack(Material.POTION, 1, (short) 16418),
            new ItemStack(Material.POTION, 1, (short) 16424), new ItemStack(Material.POTION, 1, (short) 16420),
            new ItemStack(Material.POTION, 1, (short) 16428), new ItemStack(Material.POTION, 1, (short) 16426),
            new ItemStack(Material.POTION, 1, (short) 16417), new ItemStack(Material.POTION, 1, (short) 16419),
            new ItemStack(Material.POTION, 1, (short) 16421), new ItemStack(Material.WEB, getRandom(3)),
            new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.COOKED_CHICKEN, getRandom(7)) };

    public BonusFeast(GameManager gameManager, int size) {
        super(gameManager);

        this.chestsData = new ArrayList<>();
        this.blockData = new ArrayList<>();

        location = getManager().getWorld().getHighestBlockAt(getCoord(370), getCoord(370)).getLocation();
        if (location.getY() >= 90) {
            location.setY(72);
        }
    }

    public Location getLocation() {
        return location;
    }

    public ArrayList<Location> getBlockData() {
        return blockData;
    }

    public void spawnFeast() {
        if (!location.getWorld().isChunkLoaded(location.getBlockX(), location.getBlockZ())) {
            location.getWorld().loadChunk(location.getBlockX(), location.getBlockZ());
        }
        createArea();
        createStructure();
        spawnChests();
        System.out.println(location);
        Bukkit.getOnlinePlayers()
                .forEach(online -> online.sendMessage("§cUm Feast extra spawnou em um lugar aleatório do mapa."));
    }

    @SuppressWarnings("unused")
    public void createStructure() {
        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                Location feast = new Location(location.getWorld(), location.getX() + x, location.getY(),
                        location.getZ() + z);
                if (feast.distance(location) <= 20) {

                    Location loc = feast.getBlock().getLocation().add(0, 1, 0);

                    Block remover = loc.getBlock();
                    do {
                        getManager().getWorldModifier().setBlockFast(loc, Material.AIR, (byte) 0);
                        loc.setY(loc.getY() + 1);
                        remover = loc.getBlock();
                    } while (loc.getY() < loc.getWorld().getMaxHeight());

                    getManager().getWorldModifier().setBlockFast(feast, Material.GRASS, (byte) 0);

                    blockData.add(feast);
                }
            }
        }
    }

    public void spawnChests() {

        location.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
        for (Block chests : chestsData) {

            chests.setType(Material.CHEST);

            Chest chestData = (Chest) chests.getLocation().getBlock().getState();
            int[] itens = { 310, 311, 312, 313, 276, 261, 262, 278, 279, 322, 282, 326, 327, 384, 264, 265, 364, 30, 46,
                    259, };

            addChestItems((Chest) chests.getLocation().getBlock().getState(), feastStacks);

            for (int item : itens) {
                if (new Random().nextInt(133) < 16) {
                    chestData.getInventory().setItem(new Random().nextInt(27), new ItemStack(item));
                }
            }
            if (new Random().nextInt(200) < 10) {
                chestData.getInventory().setItem(new Random().nextInt(27), new ItemStack(Material.ANVIL));
            }
        }

    }

    private void createArea() {

        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                Location feast = new Location(location.getWorld(), location.getX() + x, location.getY(),
                        location.getZ() + z);
                if (feast.distance(location) <= 20) {
                    Location loc = feast.getBlock().getLocation().add(0, 1, 0);
                    Block remover = loc.getBlock();
                    do {
                        if (loc.getY() < 130) {
                            remover.setType(Material.AIR);
                            loc.setY(loc.getY() + 1);
                            remover = loc.getBlock();
                        }
                    } while (loc.getY() < 130);
                    feast.getBlock().setType(Material.GRASS);
                }
            }
        }

        Location[] baus = { location.clone().add(1, 1, 1), location.clone().add(-1, 1, -1),
                location.clone().add(1, 1, -1), location.clone().add(-1, 1, 1), location.clone().add(-2, 1, 2),
                location.clone().add(-2, 1, -2), location.clone().add(-2, 1, 0), location.clone().add(2, 1, 0),
                location.clone().add(0, 1, -2), location.clone().add(0, 1, 2), location.clone().add(+2, 1, -2),
                location.clone().add(2, 1, 2) };
        for (Location blocos : baus) {
            chestsData.add(blocos.getBlock());
        }
    }

    public void addChestItems(Chest chest, ItemStack[] stacks) {
        for (int i = 0; i < stacks.length; i++)
            if (new Random().nextInt(100) < 9)
                chest.getInventory().setItem(new Random().nextInt(27), stacks[i]);
        if (new Random().nextInt(100) < 3) {
            chest.getInventory().setItem(new Random().nextInt(27), new ItemStack(Material.FISHING_ROD));
        }
        chest.update();
    }

    private int getRandom(int i) {
        return Math.max(1, new Random().nextInt(i + 1));
    }

    private int getCoord(int range) {
        return new Random().nextBoolean() ? -new Random().nextInt(range) : new Random().nextInt(range + 1);
    }
}
