package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Rider extends Kit {

    public Rider(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(8.0D);
        setIcon(new ItemStack(Material.SADDLE));
        setDescription("§7Vire um cavaleiro e se locomova mais rapidamente pelo mapa.");
        setItems(new ItemCreator(Material.MONSTER_EGG).setDurability(100).setName("§aRider").getStack());
    }

    public HashMap<UUID, CraftHorse> horseHashMap = new HashMap<>();

    @EventHandler
    public void onInteractRider(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if (isKitItem(e.getItem(), "§aRider") && hasKit(e.getPlayer())) {
                e.setCancelled(true);
                if (inCooldown(e.getPlayer()) || inCooldown(e.getPlayer())) {
                    sendCooldown(e.getPlayer());
                    return;
                }
                if (e.getAction().toString().contains("RIGHT")) {
                    if (horseHashMap.containsKey(e.getPlayer().getUniqueId())) {
                        if (horseHashMap.get(e.getPlayer().getUniqueId()).getTicksLived() < 3)
                            return;
                        horseHashMap.get(e.getPlayer().getUniqueId()).remove();
                        addCooldown(e.getPlayer(), 6);
                        horseHashMap.remove(e.getPlayer().getUniqueId());
                    } else {
                        Horse horse = (Horse) e.getPlayer().getWorld().spawn(e.getPlayer().getLocation(), Horse.class);
                        horseHashMap.put(e.getPlayer().getUniqueId(), ((CraftHorse) horse));
                        horse.setCustomName("§aCavalo de " + e.getPlayer().getName());
                        horse.setCustomNameVisible(true);
                        horse.setAdult();
                        horse.setStyle(Horse.Style.WHITE);
                        horse.setColor(Horse.Color.WHITE);
                        horse.setAgeLock(true);
                        horse.setBreed(false);
                        horse.setVariant(Horse.Variant.HORSE);
                        horse.setMaxHealth(46);
                        horse.setHealth(46);
                        horse.setOwner(e.getPlayer());
                        horse.setJumpStrength(1.15);
                        horse.getInventory().setSaddle(new ItemCreator(Material.SADDLE).setName("§aSela").getStack());
                        horse.setPassenger(e.getPlayer());
                        AttributeInstance attributes = (((CraftLivingEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
                        attributes.setValue(0.345);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Horse) {
            CraftHorse x = (CraftHorse) e.getEntity();
            e.getDrops().clear();
            e.setDroppedExp(0);
            UUID uuid = null;
            for (UUID uuids : horseHashMap.keySet()) {
                if (Bukkit.getPlayer(uuids) == null) continue;
                if (horseHashMap.get(uuids).equals(x)) {
                    uuid = uuids;
                    addCooldown(Bukkit.getPlayer(uuids), 10);
                }
            }
            if (uuid != null)
                horseHashMap.remove(uuid);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getVehicle() != null)
            e.getPlayer().getVehicle().eject();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (hasKit((Player) e.getEntity()) && !horseHashMap.containsKey(e.getEntity().getUniqueId())) {
                if (inCooldown((Player) e.getEntity()))
                    return;
                addCooldown((Player) e.getEntity(), 7);
            }
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Horse) {
            if (horseHashMap.containsValue((CraftHorse) e.getEntity()) && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setDamage(e.getDamage() + 2.0);
            }
        }
    }

    @EventHandler
    public void onHorse(HorseJumpEvent e) {
        e.setPower(e.getPower() + 0.1F);
    }


    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Horse) {
            if (e.getRightClicked().getPassenger() != null)
                return;
            if (horseHashMap.containsValue((CraftHorse) e.getRightClicked())) {
                if (horseHashMap.get(e.getPlayer().getUniqueId()) == null) {
                    e.getPlayer().sendMessage("§cVocê não pode montar neste cavalo.");
                    e.setCancelled(true);
                    return;
                }
                if (horseHashMap.get(e.getPlayer().getUniqueId()) != e.getRightClicked()) {
                    e.getPlayer().sendMessage("§cVocê não pode montar neste cavalo.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            if (e.getClickedInventory() instanceof HorseInventory) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClientDie(PlayerDeathEvent e) {
        if (e.getEntity().getVehicle() != null) {
            e.getEntity().eject();
        }
        if (horseHashMap.containsKey(e.getEntity().getUniqueId())) {
            horseHashMap.get(e.getEntity().getUniqueId()).remove();
            horseHashMap.remove(e.getEntity().getUniqueId());
        }
    }
}
