package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Random;

public class Specialist extends Kit {

    private HashMap<Player, SpecialistView> views = new HashMap<>();
    private Random random = new Random();

    public Specialist(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.BOOK));
        setDescription("§7Ao matar um adversário receba XP e use para encantar seus items.");
        setItems(createItemStack("§aSpecialist", Material.BOOK));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTournamentClientDie(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            if (hasKit(e.getEntity().getKiller())) {
                ItemStack itemStack = new ItemStack(Material.EXP_BOTTLE);
                Player p = e.getEntity().getKiller();
                if (Utils.firstPartial(p.getInventory(), Material.EXP_BOTTLE.getId()) == -1) {
                    if (p.getInventory().firstEmpty() == -1) {
                        p.getWorld().dropItemNaturally(p.getLocation().clone().add(0.0, 0.1, 0.0), itemStack);
                    } else {
                        p.getInventory().addItem(itemStack);
                    }
                } else {
                    p.getInventory().addItem(itemStack);
                }
            }
        }
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            if (e.getEntity().getKiller() instanceof Player && hasKit((Player) e.getEntity().getKiller())) {
                Player p = (Player) e.getEntity().getKiller();
                int i = p.getExpToLevel();
                e.setDroppedExp(0);
            }
        }
    }

    @EventHandler
    public void onFurenaceExtract(FurnaceExtractEvent e) {
        if (hasKit(e.getPlayer()))
            e.setExpToDrop(0);
    }

    @EventHandler
    public void blockbreak(BlockBreakEvent e) {
        if (hasKit(e.getPlayer())) {
            e.setExpToDrop(0);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerEnchantItem(EnchantItemEvent event) {
        event.setCancelled(false);
        event.setExpLevelCost(1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction().name().contains("RIGHT") && hasKit(e.getPlayer())
                && hasDisplay(e.getItem(), "§aSpecialist")) {
            e.setCancelled(true);

            SpecialistView view = new SpecialistView(e.getPlayer());
            view.create();
            views.put(e.getPlayer(), view);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (views.containsKey(e.getPlayer())) {
            views.get(e.getPlayer()).close();
            views.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (views.containsKey(e.getPlayer())) {
            views.get(e.getPlayer()).close();
            views.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onPrepare(PrepareItemEnchantEvent e) {
        int[] array = e.getExpLevelCostsOffered();
        array[0] = 1;
        array[1] = 2 + random.nextInt(3);
        array[2] = 3 + random.nextInt(9);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (hasKit((Player) e.getWhoClicked())) {
            ItemStack i = e.getCurrentItem();
            if (i != null) {
                ItemMeta im = i.getItemMeta();
                if (im instanceof Repairable) {
                    ((Repairable) im).setRepairCost(0);
                }
            }
        }
    }

    public class SpecialistView {

        private Player player;
        private Block block;

        public SpecialistView(Player player) {
            this.player = player;
        }

        public void create() {
            Location base = player.getLocation();
            Block block = base.getWorld().getBlockAt(base.getBlockX(), 190, base.getBlockZ());

            while (block.getType() != Material.AIR) {
                block = block.getRelative(0, 1, 0);
            }
            block.setType(Material.ENCHANTMENT_TABLE);
            this.block = block;
            player.openEnchanting(block.getLocation(), true);
        }

        public void close() {
            block.setType(Material.AIR);
        }
    }
}
