package br.com.stenox.cxc.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
public enum Items {

    CXC(new ItemCreator[] {new ItemCreator(Material.NAME_TAG).setName("§aEscolha seu time").setDescription("§7Clique com o botão direito")}, new Integer[] {4}),
    SPAWN(new ItemCreator[] {new ItemCreator(Material.CHEST).setName("§aEscolha seu kit")}, new Integer[] {0}),
    INVINCIBILITY(new ItemCreator[] {new ItemCreator(Material.COMPASS)}, new Integer[] {0}),
    IN_GAME(new ItemCreator[] {new ItemCreator(Material.COMPASS)}, new Integer[] {0}),
    DEAD(new ItemCreator[] {new ItemCreator(Material.COMPASS).setName("§aTeleportar para um Jogador")}, new Integer[] {0});

    private ItemCreator[] items;
    private Integer[] slots;

    Items(ItemCreator[] items, Integer[] slots) {
        this.items = items;
        this.slots = slots;
    }

    public ItemCreator getItem(int id) {
        return id <= items.length - 1 ? items[id] : items[0];
    }

    public void build(Player player) {
        if (slots != null) {
            int id = 0;
            for (Integer slot : slots) {
                if (getItem(id).getStack().getType() == Material.SKULL_ITEM)
                    getItem(id).setSkull(player.getName());
                getItem(id).build(player.getInventory(), slot);
                id++;
            }
        } else {
            for (int i = 0; i < items.length; i++) {
                if (getItem(i).getStack().getType() == Material.SKULL_ITEM)
                    getItem(i).setSkull(player.getName());
                getItem(i).build(player.getInventory());
            }
        }
    }
}
