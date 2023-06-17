package br.com.stenox.cxc.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DamageListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();
		ItemStack sword = player.getItemInHand();

		double damage = event.getDamage();
		double swordDamage = getDamage(sword.getType());

		boolean isMore = damage > 1;

		if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
					double minus;
					if (isCritical(player))
						minus = (swordDamage + (swordDamage / 2)) * 1.3 * (effect.getAmplifier() + 1);
					else
						minus = swordDamage * 1.3 * (effect.getAmplifier() + 1);

					damage = damage - minus;
					damage += 2 * (effect.getAmplifier() + 1);
					break;
				}
			}
		}
		if (!sword.getEnchantments().isEmpty()) {
			if (sword.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS) && isArthropod(event.getEntityType())) {
				damage = damage - (1.5 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS));
				damage += sword.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
			}
			if (sword.containsEnchantment(Enchantment.DAMAGE_UNDEAD) && isUndead(event.getEntityType())) {
				damage = damage - (1.5 * sword.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD));
				damage += sword.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
			}
			if (sword.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage = damage - 1.25 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
				damage += sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}
		if (isCritical(player)) {
			damage = damage - (swordDamage / 2);
			damage += 1;
		}
		if (isMore)
			damage -= 2;

		event.setDamage(damage);
	}

	@SuppressWarnings("deprecation")
	private boolean isCritical(Player player) {
		return player.getFallDistance() > 0 && !player.isOnGround() && !player.hasPotionEffect(PotionEffectType.BLINDNESS);
	}

	private boolean isArthropod(EntityType type) {
		switch (type) {
			case CAVE_SPIDER:
			case SPIDER:
			case SILVERFISH:
				return true;
			default:
				break;
		}
		return false;
	}

	private boolean isUndead(EntityType type) {
		switch (type) {
			case SKELETON:
			case ZOMBIE:
			case WITHER_SKULL:
			case PIG_ZOMBIE:
				return true;
			default:
				break;
		}
		return false;
	}

	private double getDamage(Material type) {
		double damage = 1.0;
		if (type.toString().contains("DIAMOND_")) {
			damage = 7.0;
		} else if (type.toString().contains("IRON_")) {
			damage = 5.0;
		} else if (type.toString().contains("STONE_")) {
			damage = 4.0;
		} else if (type.toString().contains("WOOD_")) {
			damage = 3.0;
		} else if (type.toString().contains("GOLD_")) {
			damage = 3.0;
		}
		if (!type.toString().contains("_SWORD")) {
			damage--;
			if (!type.toString().contains("_AXE")) {
				damage--;
				if (!type.toString().contains("_PICKAXE")) {
					damage--;
					if (!type.toString().contains("_SPADE")) {
						damage = 1.0;
					}
				}
			}
		}
		return damage;
	}

	@EventHandler
	public void onLava(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.LAVA) {
			e.setDamage(4.0D);
		}
	}
}