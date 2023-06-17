package br.com.stenox.cxc.gamer.menu;

import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.utils.menu.PaginatedMenu;
import br.com.stenox.cxc.utils.menu.PlayerMenuUtility;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayersMenu extends PaginatedMenu {

    private final List<Gamer> gamers;

    public PlayersMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility, 21);

        gamers = new ArrayList<>(Main.getInstance().getGamerProvider().getAliveGamers());
    }

    @Override
    public String getMenuName() {
        return "Jogadores";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        Player player = (Player) event.getWhoClicked();

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§aPágina Anterior")){
            if (page > 0){
                page = page - 1;
                super.open(new PlayerMenuUtility(player));
            }
        } else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§aPágina Posterior")){
            if (!((index + 1) >= gamers.size())){
                page = page + 1;
                super.open(new PlayerMenuUtility(player));
            }
        } else {
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            Player target = Bukkit.getPlayer(meta.getOwner());

            player.closeInventory();

            if (target == null){
                player.sendMessage("§cJogador inválido.");
                return;
            }
            player.teleport(target);
            player.sendMessage("§aTeleportado até o jogador com sucesso.");
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(27, new ItemCreator()
                .setMaterial(Material.ARROW)
                .setName("§aPágina Anterior")
                .getStack());

        inventory.setItem(35, new ItemCreator()
                .setMaterial(Material.ARROW)
                .setName("§aPágina Posterior")
                .getStack());

        int slot = 10, last = slot;

        for (int i = 0; i < getMaxItemsPerPage(); i++){
            index = getMaxItemsPerPage() * page + i;

            if (index >= gamers.size()) break;

            Gamer gamer = gamers.get(index);
            if (gamer == null) continue;

            ItemStack stack = new ItemCreator()
                    .setMaterial(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setSkull(gamer.getName())
                    .setName(gamer.getTag().getPrefix() + gamer.getName())
                    .setDescription("§7Kills: §a" + gamer.getKills(), "", "§eClique para teleportar.")
                    .getStack();

            inventory.setItem(slot, stack);

            slot++;
            if (slot == (last + 7)){
                slot+=2;
                last = slot;
            }
        }
    }
}
