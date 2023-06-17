package br.com.stenox.cxc.kit.manager;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ClassGetter;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitManager {

    private final List<Kit> kits;
    private final List<String> items;

    private final GameManager manager;

    @Getter
    private List<String> freeKits = Arrays.asList("monk", "fireman", "cannibal", "flash", "timelord", "surprise");

    public KitManager(GameManager gameManager) {
        this.kits = new ArrayList<>();
        this.items = new ArrayList<>();
        this.manager = gameManager;
    }

    public void loadKits() {
        for (Class<?> c : ClassGetter.getClassesForPackage(Main.getInstance(), "br.com.stenox.cxc.kit.list")) {
            if (Kit.class.isAssignableFrom(c) && (c != Kit.class)) {
                try {
                    Kit kit = (Kit) c.getConstructor(GameManager.class).newInstance(manager);

                    Bukkit.getPluginManager().registerEvents(kit, Main.getInstance());

                    if (kit.isActive()) {
                        kits.add(kit);
                        if (kit.getItems() != null) {
                            for (ItemStack item : kit.getItems()) {
                                if (item != null) {
                                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        items.add(item.getItemMeta().getDisplayName());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void giveItems(Gamer gamer){
        if (gamer.getKit() != null)
            gamer.getKit().give(gamer.getPlayer());

        if (!gamer.getPlayer().getInventory().contains(Material.COMPASS))
            gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
    }

    public boolean isKitItem(Kit kit, ItemStack item) {
        if (Main.getInstance().getGame().getStage() == GameStage.ENDING)
            return false;
        if (kit == null)
            return false;
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && (item.getItemMeta().getDisplayName().startsWith("§a") && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equalsIgnoreCase("§aBússola")))
            return true;
        for (String string : items) {
            if (new ItemCreator(Material.AIR).checkItem(item, string)) {
                return true;
            }
        }
        return false;
    }

    public Kit searchKit(String name){
        return kits.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
