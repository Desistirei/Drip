package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Urgal extends Kit {

    public Urgal(GameManager gameManager) {
        super(gameManager);
        setIcon(getUrgalIcon(1));
        setDescription("§7Receba três poções de Força II no começo da partida.");
        setItems(new ItemCreator(getUrgalIcon(3)).getStack());
    }

    @EventHandler
    public void aaa(PlayerItemConsumeEvent e) {
        if (hasKit(e.getPlayer()) && isKitItem(e.getItem(), "§aScout")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void a(PlayerInteractEvent e) {
        if (hasKit(e.getPlayer()) && isKitItem(e.getItem(), "§aUrgal") && e.getAction().toString().contains("RIGHT")) {
            if (e.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                e.getPlayer().sendMessage("§cVocê já possui um efeito de força ativo.");
                e.setCancelled(true);
                return;
            }
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (10 * 20), 1), true);
            e.getPlayer().sendMessage("§aVocê recebeu efeito de Força II  durante 10 segundos.");
            if (e.getItem().getAmount() > 1) {
                e.getPlayer().getItemInHand().setAmount(e.getItem().getAmount() - 1);
            } else {
                e.getPlayer().setItemInHand(null);
            }
            e.setCancelled(true);
        }
    }

    private ItemStack getUrgalIcon(int amount) {
        Potion potion = new Potion(PotionType.STRENGTH);
        ItemStack stack = potion.toItemStack(amount);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        meta.setDisplayName("§aUrgal");
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 0), true);
        stack.setItemMeta(meta);
        return stack;
    }
}
