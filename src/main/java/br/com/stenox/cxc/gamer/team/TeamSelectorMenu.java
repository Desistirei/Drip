package br.com.stenox.cxc.gamer.team;

import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.utils.menu.Menu;
import br.com.stenox.cxc.gamer.Gamer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamSelectorMenu extends Menu {
    
    private final List<GamerTeam> teams;
    
    public TeamSelectorMenu(){
        teams = new ArrayList<>();

        Arrays.stream(GamerTeam.values()).filter(GamerTeam::isEnabled).forEach(teams::add);
    }

    @Override
    public String getMenuName() {
        return "Seletor de Times";
    }

    @Override
    public int getSlots() {
        return teams.size() <= 5 ? 27 : 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
            return;

        Gamer gamer = Gamer.getGamer(event.getWhoClicked().getUniqueId());

        for (GamerTeam team : GamerTeam.values()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(team.getName()))
                gamer.setTeam(team);
        }

        event.getWhoClicked().closeInventory();
        gamer.sendMessage("§eVocê selecionou o time: " + gamer.getTeam().getColor() + gamer.getTeam().getName());
    }

    @Override
    public void setMenuItems() {
        int slot = 11, last = slot;

        for (GamerTeam team : teams) {
            if (slot == last+5){
                slot+=4;
                last = slot;
            }

            inventory.setItem(slot, new ItemCreator()
                    .setMaterial(Material.LEATHER_CHESTPLATE)
                    .setColor(team.getLeatherColor())
                    .setName(team.getColor() + "Time " + team.getName())
                    .setDescription("§7Clique com o botão direito para selecionar o time.")
                    .getStack());

            slot++;
        }
    }
}
