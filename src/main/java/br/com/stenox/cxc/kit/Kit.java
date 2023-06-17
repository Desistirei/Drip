package br.com.stenox.cxc.kit;

import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Kit implements Listener {

    private static final ItemCreator itemBuilder = new ItemCreator();

    private final String name;
    private String description;

    private boolean active;

    private double cooldownSeconds;
    private double combatCooldownSeconds;

    private ItemStack icon;

    private final List<ItemStack> items;

    private final GameManager manager;

    private final ConcurrentHashMap<UUID, Long> cooldown = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> combatCooldown = new ConcurrentHashMap<>();

    private final DecimalFormat FORMATTER = new DecimalFormat("#.#");

    public Kit(GameManager manager) {
        this.manager = manager;
        this.active = true;
        this.name = getClass().getSimpleName();
        this.items = new ArrayList<>();
    }

    public Kit(GameManager manager, int cooldownSeconds, ItemStack icon, ItemStack... items) {
        this.manager = manager;
        this.active = true;
        this.cooldownSeconds = cooldownSeconds;
        this.name = getClass().getSimpleName();

        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(items));

        this.icon = icon;

        ItemMeta meta = this.icon.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + this.name);

        this.icon.setItemMeta(meta);
    }

    @EventHandler
    public void onTimeSecond(TimeSecondEvent event) {
        if (event.getType() == TimeSecondEvent.TimeType.MILLISECONDS) {
            for (UUID uuid : cooldown.keySet()) {
                double seconds = ((cooldown.get(uuid) / 1000.0D) + cooldownSeconds) - (System.currentTimeMillis() / 1000.0D);
                if (seconds <= 0.0D) {
                    cooldown.remove(uuid);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null)
                        player.sendMessage("§aAcabou o cooldown do seu kit.");
                }
            }

            for (UUID uuid : combatCooldown.keySet()) {
                double seconds = ((combatCooldown.get(uuid) / 1000.0D) + combatCooldownSeconds) - (System.currentTimeMillis() / 1000.0D);
                if (seconds <= 0.0D)
                    combatCooldown.remove(uuid);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public long getCooldown(Player player) {
        return cooldown.containsKey(player.getUniqueId()) ? 0 : cooldown.get(player.getUniqueId());
    }

    public long getCombatCooldown(Player player) {
        return combatCooldown.containsKey(player.getUniqueId()) ? 0 : combatCooldown.get(player.getUniqueId());
    }

    public String getDescription() {
        return description;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public GameManager getManager() {
        return this.manager;
    }

    public Gamer getGamer(Player player) {
        if (player == null || player.getUniqueId() == null) return null;
        return Gamer.getGamer(player.getUniqueId());
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setItems(ItemStack... itens) {
        this.items.addAll(Arrays.asList(itens));
    }

    public void setCooldownSeconds(double cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public void setCombatCooldownSeconds(double combatCooldownSeconds) {
        this.combatCooldownSeconds = combatCooldownSeconds;
    }

    public double getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void addCooldown(Player player) {
        cooldown.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void addCombatCooldown(Player player, double seconds) {
        combatCooldown.put(player.getUniqueId(), System.currentTimeMillis());

        this.combatCooldownSeconds = seconds;
    }

    public void addCooldown(Player player, double seconds) {
        cooldown.put(player.getUniqueId(), System.currentTimeMillis());

        this.cooldownSeconds = seconds;
    }

    public void sendCooldown(Player player) {
        double seconds = ((cooldown.get(player.getUniqueId()) / 1000.0) + this.cooldownSeconds) - (System.currentTimeMillis() / 1000.0);
        player.sendMessage("§cVocê está em cooldown, aguarde " + FORMATTER.format(seconds) + " segundos.");
    }

    public void sendCombatCooldown(Player player) {
        double seconds = ((combatCooldown.get(player.getUniqueId()) / 1000.0) + this.combatCooldownSeconds) - (System.currentTimeMillis() / 1000.0);
        player.sendMessage("§cVocê está em combate, aguarde " + FORMATTER.format(seconds) + "s.");
    }

    public boolean hasDisplay(ItemStack item, String display) {
        if (item == null || !item.hasItemMeta())
            return false;
        if (!item.getItemMeta().hasDisplayName())
            return false;
        return item.getItemMeta().getDisplayName().equalsIgnoreCase(display);
    }

    public void give(Player player) {
        if (this.items == null) {
            return;
        }

        for (ItemStack item : this.items) {
            player.getInventory().addItem(item);
        }
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public boolean inCooldown(Player player) {
        return cooldown.containsKey(player.getUniqueId());
    }

    public boolean inCombatCooldown(Player player) {
        return combatCooldown.containsKey(player.getUniqueId());
    }

    public void removeCooldown(Player player) {
        cooldown.remove(player.getUniqueId());
    }

    public void removeCombatCooldown(Player player) {
        combatCooldown.remove(player.getUniqueId());
    }

    public boolean isPlayer(Entity entity) {
        return entity instanceof Player;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isStarting() {
        return Main.getInstance().getGame().getStage() == GameStage.STARTING;
    }

    public boolean isInvincibility() {
        return Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY;
    }

    public boolean hasKit(Gamer gamer) {
        if (gamer.getKit() == null) return false;
        return (name.equals(gamer.getKitName()));
    }

    public boolean hasKit(Player player) {
        Gamer gamer = getGamer(player);
        if (gamer == null || gamer.getKit() == null) return false;
        return name.equals(gamer.getKit().getName());
    }

    public boolean isKitItem(ItemStack item, Material material, String name) {
        return ((item != null) && (item.getType() == material) && (item.hasItemMeta()) && (item.getItemMeta().hasDisplayName())
                && (item.getItemMeta().getDisplayName().equals(name)));
    }

    public boolean isKitItem(ItemStack item, String name) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(name));
    }

    public static ItemStack createItemStack(String name, Material material) {
        return itemBuilder.setMaterial(material).setName(name).getStack();
    }
}
