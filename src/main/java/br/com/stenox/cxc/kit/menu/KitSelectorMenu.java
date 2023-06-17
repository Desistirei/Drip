package br.com.stenox.cxc.kit.menu;

import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.utils.menu.PaginatedMenu;
import br.com.stenox.cxc.utils.menu.PlayerMenuUtility;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.kit.manager.KitManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitSelectorMenu extends PaginatedMenu {

    private final List<Kit> kits;

    private final Gamer gamer;

    public KitSelectorMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility, 21);

        KitManager kitManager = Main.getInstance().getKitManager();

        kits = new ArrayList<>();

        gamer = Gamer.getGamer(playerMenuUtility.getOwner().getUniqueId());

        for (Kit kit : kitManager.getKits()) {
            if (kits.contains(kit) || !kit.isActive())
                continue;

            if (Variable.ALL_KITS_FREE.isActive()){
                kits.add(kit);
                continue;
            }

            if (gamer.getGroup().ordinal() <= Group.SNOW.ordinal()){
                kits.add(kit);
            } else {
                if (kitManager.getFreeKits().contains(kit.getName().toLowerCase())){
                    kits.add(kit);
                }
            }
        }
    }


    @Override
    public String getMenuName() {
        return "Seletor de Kits";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
            return;

        Player player = (Player) event.getWhoClicked();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        ItemStack item = event.getCurrentItem();

        if (item.getItemMeta().getDisplayName().equals("§aPágina Anterior")){
            if (page > 0){
                page = page - 1;
                super.open();
            }
        } else if (item.getItemMeta().getDisplayName().equals("§aPágina Posterior")){
            if (!((index + 1) >= kits.size())){
                page = page + 1;
                super.open();
            }
        } else if (item.getItemMeta().getDisplayName().startsWith("§aKit ")){
            if (Main.DEFAULT_KIT != null){
                player.closeInventory();
                player.sendMessage("§cVocê não pode alterar o kit.");
                return;
            }
            for (Kit kit : kits){
                if (!kit.isActive()){
                    player.closeInventory();
                    player.sendMessage("§cEste kit está desativado.");
                    return;
                }
                if (kit.getName().equalsIgnoreCase(item.getItemMeta().getDisplayName().replace("§aKit ", ""))){
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                    gamer.setKit(kit);
                    gamer.sendTitle("§a" + kit.getName(), "§fSelecionado.");
                    player.sendMessage("§aVocê selecionou o kit " + kit.getName());
                    return;
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        int slot = 10, last = slot;

        inventory.setItem(45, new ItemCreator(Material.ARROW).setName("§aPágina Anterior").getStack());
        inventory.setItem(53, new ItemCreator(Material.ARROW).setName("§aPágina Posterior").getStack());

        inventory.setItem(49, new ItemCreator(gamer.getKit() == null ? Material.BARRIER : gamer.getKit().getIcon().getType()).setName("§eKit selecionado: §a" + gamer.getKitName()).getStack());

        for (int i = 0; i < getMaxItemsPerPage(); i++){
            index = getMaxItemsPerPage() * page + i;
            if (index >= kits.size()) break;

            Kit kit = kits.get(index);

            ItemStack item = new ItemCreator(kit.getIcon()).setName("§aKit " + kit.getName()).setDescription(ItemCreator.getFormattedLore(kit.getDescription())).getStack();

            inventory.setItem(slot, item);

            slot++;
            if (slot == (last+7)){
                slot += 2;
                last = slot;
            }
        }
    }
}
