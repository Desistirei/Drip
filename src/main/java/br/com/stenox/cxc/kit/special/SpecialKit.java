package br.com.stenox.cxc.kit.special;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SpecialKit {

    public static Set<SpecialKit> specialKits = new HashSet<>();

    public ItemStack[] armorContents;
    public ItemStack[] contents;
    public Collection<PotionEffect> effects;
    private String name;
    private String creator;

    public SpecialKit(String name2, ItemStack[] contents2, ItemStack[] armorContents2, String creator) {
        this.name = name2;
        this.contents = contents2;
        this.armorContents = armorContents2;
        this.creator = creator;
    }

    public String getName() {
        return this.name;
    }

    public void refreshItems() {
        for (ItemStack contents : this.contents) {
            if (contents.getAmount() == 0)
                contents.setAmount(1);
        }
        for (ItemStack contents : this.armorContents) {
            if (contents.getAmount() == 0)
                contents.setAmount(1);
        }
    }

    public String getCreator() {
        return this.creator;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public static void createKit(SpecialKit kit) {
        specialKits.add(kit);
    }

    public static void deleteKit(String kit) {
        specialKits.remove(SpecialKit.getKit(kit));
    }

    public static SpecialKit getKit(String name) {
        return specialKits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}