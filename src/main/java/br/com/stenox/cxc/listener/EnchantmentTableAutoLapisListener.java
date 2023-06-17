package br.com.stenox.cxc.listener;

import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class EnchantmentTableAutoLapisListener implements Listener {

    public static ItemStack lapis;

    public static void init() {
        Dye d = new Dye();
        d.setColor(DyeColor.BLUE);
        lapis = d.toItemStack();
        lapis.setAmount(64);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            if (e.getSlot() == 1) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        e.getInventory().setItem(1, lapis);
    }
}
