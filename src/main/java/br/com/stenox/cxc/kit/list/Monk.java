package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Monk extends Kit {

    public Monk(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(15.0D);
        setIcon(new ItemStack(Material.BLAZE_ROD));
        setDescription("§7Bagunce o inventário de seu adversário.");
        setItems(new ItemCreator(Material.BLAZE_ROD).setName("§aMonk").getStack());
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player
                && isKitItem(e.getPlayer().getItemInHand(), Material.BLAZE_ROD, "§aMonk") && hasKit(e.getPlayer())) {

            Player clicked = (Player) e.getRightClicked();
            Player player = e.getPlayer();

            if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                player.sendMessage("§cVocê não pode usar o kit Monk na invencibilidade.");
                return;
            }
            if (inCooldown(player)) {
                sendCooldown(player);
                return;
            }

            addCooldown(player);

            int newSlot = new Random().nextInt(36);

            ItemStack now = (clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null);
            ItemStack random = (clicked.getInventory().getItem(newSlot) != null ? clicked.getInventory().getItem(newSlot).clone() : null);

            clicked.getInventory().setItem(newSlot, now);

            if (random == null)
                clicked.setItemInHand(null);
            else
                clicked.getInventory().setItemInHand(random);

            player.sendMessage("§aVocê bagunçou o inventário do jogador " + clicked.getName());
        }
    }
}
