package br.com.stenox.cxc.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemCreator {

    private ItemStack itemStack;

    public ItemCreator(Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemCreator(ItemStack stack) {
        itemStack = stack;
    }

    public ItemCreator() {
    }

    public ItemCreator setMaterial(Material type) {
        itemStack = new ItemStack(type);
        return this;
    }

    public ItemCreator setFast(Material type, String name, int data) {
        setMaterial(type);
        setName(name);
        setDurability(data);
        return this;
    }

    public ItemCreator setFast(Material type, String name) {
        setMaterial(type);
        setName(name);
        return this;
    }

    public ItemCreator setType(Material type) {
        setMaterial(type);
        return this;
    }

    public ItemCreator setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemCreator setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemCreator addItemFlag(ItemFlag... flag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator setDescription(List<String> desc) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(desc);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator setDescription(String... desc) {
        setDescription(Arrays.asList(desc));
        return this;
    }

    public ItemCreator setDescription(String text) {
        List<String> lore = getFormattedLore(text);
        setDescription(lore.toArray(new String[]{}));
        return this;
    }

    public ItemCreator glow() {
        return this;
    }

    public ItemCreator setEnchant(Enchantment[] enchant, int[] level) {
        for (int i = 0; i < enchant.length; ++i) {
            itemStack.addUnsafeEnchantment(enchant[i], level[i]);
        }
        return this;
    }

    public ItemCreator setEnchant(Enchantment enchant, int level) {
        itemStack.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public ItemCreator setUnbreakable() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator setBreakable() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(false);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator setBreakable(boolean breakable) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(!breakable);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemCreator build(Player player, int... slot) {
        build(player.getInventory(), slot);
        player.updateInventory();
        return this;
    }

    public ItemCreator noAttributes() {
        return this;
    }

    public ItemCreator build(Player player) {
        player.getInventory().addItem(itemStack);
        player.updateInventory();
        return this;
    }

    public ItemCreator build(Inventory inventory, int... slot) {
        for (int slots : slot) {
            inventory.setItem(slots, itemStack);
        }

        return this;
    }

    public ItemCreator build(Inventory inventory) {
        inventory.addItem(itemStack);
        return this;
    }

    public ItemStack getStack() {
        return itemStack;
    }

    public ItemMeta setName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        return meta;
    }

    public ItemCreator setSkull(String owner) {
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(owner);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack setColor(Material material, Color color) {
        ItemStack stack = new ItemStack(material);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) stack.getItemMeta();
        armorMeta.setColor(color);
        stack.setItemMeta(armorMeta);
        return stack;
    }

    public ItemCreator setColor(Color color) {
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) getStack().getItemMeta();
        armorMeta.setColor(color);
        getStack().setItemMeta(armorMeta);
        return this;
    }

    public ItemStack setColor(Material material, Color color, String name) {
        ItemStack stack = new ItemStack(material);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) stack.getItemMeta();
        armorMeta.setColor(color);
        armorMeta.setDisplayName(name);
        stack.setItemMeta(armorMeta);
        return stack;
    }

    public ItemCreator setColor(Color color, String name) {
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        armorMeta.setColor(color);
        armorMeta.setDisplayName(name);
        itemStack.setItemMeta(armorMeta);
        return this;
    }

    public ItemCreator chanceItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public boolean checkItem(ItemStack item, String display) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equalsIgnoreCase(display));
    }

    public boolean checkContains(ItemStack item, String display) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(display));
    }

    public static List<String> getFormattedLore(String text) {
        List<String> lore = new ArrayList<>();
        String[] split = text.split(" ");
        text = "";

        for (int i = 0; i < split.length; ++i) {
            if (ChatColor.stripColor(text).length() > 25 || ChatColor.stripColor(text).endsWith(".")
                    || ChatColor.stripColor(text).endsWith("!")) {
                lore.add("§7" + text);
                if (text.endsWith(".") || text.endsWith("!")) {
                    lore.add("");
                }
                text = "";
            }
            String toAdd = split[i];
            if (toAdd.contains("\n")) {
                toAdd = toAdd.substring(0, toAdd.indexOf("\n"));
                split[i] = split[i].substring(toAdd.length() + 1);
                lore.add("§7" + text + ((text.length() == 0) ? "" : " ") + toAdd);
                text = "";
                --i;
            } else {
                text += ((text.length() == 0) ? "" : " ") + toAdd;
            }
        }
        lore.add("§7" + text);

        return lore;
    }
}
