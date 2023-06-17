package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Scout extends Kit {

    private static ItemStack scoutIcon() {
        ItemStack itemStack = new ItemStack(Material.POTION, 1, (short) 8226);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName("§aScout");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (hasKit(event.getPlayer()) && isKitItem(event.getItem(), "§aScout")) {
            event.setCancelled(true);
        }
    }

    public Scout(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(30.0D);
        setIcon(new ItemCreator().setMaterial(Material.POTION).setDurability(8226).setAmount(1).setName("§aScout").getStack());
        setDescription("§7Use sua poção para ganhar velocidade.");
        setItems(scoutIcon());
    }

    @EventHandler
    public void a(PlayerInteractEvent e) {
        if (hasKit(e.getPlayer()) && isKitItem(e.getItem(), "§aScout") && e.getAction().toString().contains("RIGHT")) {
            if (inCooldown(e.getPlayer())) {
                e.setCancelled(true);
                sendCooldown(e.getPlayer());
                return;
            }
            if (e.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
                e.getPlayer().sendMessage("§cVocê já possui um efeito de velocidade ativo.");
                e.setCancelled(true);
                return;
            }
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 20), 1), true);
            e.getPlayer().sendMessage("§aVocê recebeu efeito de Velocidade II durante 20 segundos.");
            e.setCancelled(true);
            addCooldown(e.getPlayer());
        }
    }
}
