package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Random;

public class Magic extends Kit {

    public Magic(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(15.0D);
        setIcon(new ItemCreator().setType(Material.DIAMOND_HOE).setName("§aMagic").setEnchant(Enchantment.DURABILITY, 1).getStack());
        setItems(new ItemCreator().setType(Material.DIAMOND_HOE).setName("§aMagic").getStack());
        setDescription("§7Lance poções boas ou ruins em seus adversários.");
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.hasItem())
            return;
        if (!isKitItem(e.getItem(), "§aMagic"))
            return;
        if (!hasKit(e.getPlayer()))
            return;

        if (inCooldown(e.getPlayer())) {
            sendCooldown(e.getPlayer());
            return;
        }
        PotionEffectType[] badEffects = new PotionEffectType[]{PotionEffectType.WEAKNESS, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.HARM};
        PotionEffectType[] goodEffects = new PotionEffectType[]{PotionEffectType.SPEED, PotionEffectType.REGENERATION, PotionEffectType.ABSORPTION, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.DAMAGE_RESISTANCE};
        if (e.getAction().name().contains("RIGHT")) {
            PotionEffectType type = Arrays.asList(goodEffects).get(new Random().nextInt(goodEffects.length - 1));
            Player p = e.getPlayer();
            Potion potion = new Potion(PotionType.getByEffect(type));
            potion.setSplash(true);
            potion.setType(PotionType.getByEffect(type));
            ItemStack item = potion.toItemStack(1);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setMainEffect(type);
            meta.addCustomEffect(new PotionEffect(type, (20 * 15), 1), true);
            item.setItemMeta(meta);
            ThrownPotion thrownPotion = p.launchProjectile(ThrownPotion.class);
            thrownPotion.setItem(item);
            thrownPotion.getEffects().add(new PotionEffect(type, (20 * 15), 1));
            thrownPotion.setFallDistance(p.getEyeLocation().getDirection().getBlockY());
            addCooldown(p);
        } else if (e.getAction().name().contains("LEFT")) {
            PotionEffectType type = Arrays.asList(badEffects).get(new Random().nextInt(badEffects.length - 1));
            Player p = e.getPlayer();
            Potion potion = new Potion(PotionType.getByEffect(type));
            potion.setSplash(true);
            potion.setType(PotionType.getByEffect(type));
            ItemStack item = potion.toItemStack(1);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setMainEffect(type);
            meta.addCustomEffect(new PotionEffect(type, (20 * 7), 0), true);
            item.setItemMeta(meta);
            ThrownPotion thrownPotion = p.launchProjectile(ThrownPotion.class);
            thrownPotion.setItem(item);
            thrownPotion.getEffects().add(new PotionEffect(type, (20 * 15), 1));
            thrownPotion.setFallDistance(p.getEyeLocation().getDirection().getBlockY());
            thrownPotion.setVelocity(thrownPotion.getVelocity().clone().multiply(1.62));
            addCooldown(p);
        }
    }
}
