package br.com.stenox.cxc.bo3.reader;

import br.com.stenox.cxc.bo3.BO3Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class BO3Reader {

    private File file;
    private List<BO3Block> blocks = new ArrayList<>();

    public BO3Reader(String file) {
        this(new File(file));
    }

    public BO3Reader(File file) {
        this.file = file;
    }

    public void spawn(Location location) {
        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                if (line.startsWith("#"))
                    continue;
                if (line.startsWith("Block(")) {
                    line = line.substring("Block(".length(), line.length() - 1);

                    String[] arr = line.split(",");
                    int x = Integer.parseInt(arr[0]);
                    int y = Integer.parseInt(arr[1]);
                    int z = Integer.parseInt(arr[2]);

                    String rawMats = arr[3];

                    Material material = null;
                    byte data = 0;

                    if (rawMats.contains(":")) {
                        String[] mats = arr[3].split(":");
                        material = Material.valueOf(mats[0]);
                        data = Byte.parseByte(mats[1]);
                    } else {
                        material = Material.valueOf(rawMats);
                    }

                    blocks.add(new BO3Block(x, y, z, material, data));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (BO3Block bo3 : blocks) {
            Block block = location.clone().add(bo3.getX(), bo3.getY(), bo3.getZ()).getBlock();
            block.setTypeIdAndData(bo3.getMaterial().getId(), bo3.getData(), false);
            onSpawn(block);
        }
    }

    public abstract void onSpawn(Block block);
}
