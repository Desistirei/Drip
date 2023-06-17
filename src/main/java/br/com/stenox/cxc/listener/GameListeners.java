package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.event.game.GameChangeStageEvent;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.game.structures.types.Feast;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.gamer.state.GamerState;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import com.avaje.ebeaninternal.server.el.ElSetValue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameListeners implements Listener {

    @EventHandler
    public void onGameChangeStage(GameChangeStageEvent event) {
        if (event.getNewStage() == GameStage.INVINCIBILITY) {
            Main.getInstance().getGamerProvider().getGamers().forEach(gamer -> {
                Player player = gamer.getPlayer() == null ? Bukkit.getPlayer(gamer.getUniqueId()) : gamer.getPlayer();

                if (player != null) {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);

                    player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);

                    if (Main.DEFAULT_KIT != null)
                        gamer.setKit(Main.DEFAULT_KIT);
                }
            });

            surprise();

            Main.getInstance().getGameManager().giveItems();
        } else if (event.getNewStage() == GameStage.IN_GAME)
            Bukkit.broadcastMessage("§cA partida iniciou!");
        else if (event.getNewStage() == GameStage.ENDING) {
            Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
                int i = 0;

                public void run() {
                    i++;
                    if (i == 25)
                        Bukkit.shutdown();
                }
            }, 0L, 20L);
        }
    }

    public void surprise() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Gamer gamer = Gamer.getGamer(p.getUniqueId());
            if (gamer == null) continue;
            if (gamer.getKitName().equalsIgnoreCase("Surprise")) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    Kit kit = Main.getInstance().getGamerProvider().getRandomKit();

                    gamer.setKit(kit);

                    p.sendMessage("§aO kit Surprise escolheu o kit " + kit.getName() + " para você!");
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onTimeSecond(TimeSecondEvent event) {
        for (Gamer gamers : Main.getInstance().getGamerProvider().getAliveGamers()) {
            if (gamers.isFighting())
                gamers.setFighting(gamers.getFighting() - 1);

            if (gamers.getFighting() <= 0)
                gamers.setLastCombat(null);
        }
    }

    //Feast
    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (e.isCancelled())
            return;
        if (Main.getInstance().getGame().getTimer().getFeast() != null) {
            for (Block block : e.blockList()) {
                if (Main.getInstance().getGame().getTimer().getFeast().chestsData.contains(block)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (Main.getInstance().getGame().getTimer().getFeast() != null) {
            if (Feast.blockData.contains(e.getBlock().getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        if (gamer.isAlive()) {
            if (Main.getInstance().getGame().getMode() == br.com.stenox.cxc.game.mode.GameMode.EVENT && Main.getInstance().getGame().getTime() < 300 && Variable.RESPAWN.isActive()) {
                List<ItemStack> drops = new ArrayList<>(event.getDrops());

                event.getDrops().forEach(item -> {
                    if (gamer.getKit() != null && Main.getInstance().getKitManager().isKitItem(gamer.getKit(), item)) {
                        drops.remove(item);
                    }
                });

                event.getDrops().clear();
                drops.forEach(drop -> {
                    if (drop != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), drop);
                    }
                });

                int x = new Random().nextInt(300);
                int z = new Random().nextInt(300);

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    player.setLevel(0);
                    player.setMaxHealth(20.0D);
                    player.setHealth(20.0D);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                    player.setItemOnCursor(null);
                    player.setFallDistance(-1.0F);
                    player.getInventory().clear();
                    player.getInventory().setHeldItemSlot(0);
                    player.getInventory().setArmorContents(null);
                    player.getOpenInventory().getTopInventory().clear();
                    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

                    Main.getInstance().getGameManager().giveItems(gamer);

                    player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
                }, 2L);

                player.sendMessage("§aVocê renasceu.");

                if (player.getKiller() != null) {
                    Gamer killer = Gamer.getGamer(player.getKiller().getUniqueId());
                    killer.addKill();
                }

                event.setDeathMessage(null);
                return;
            }

            player.setHealth(20.0D);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(player.getLocation().add(0, 0.75, 0));
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 2));

            if (gamer.getGroup().ordinal() <= Group.YOUTUBER_PLUS.ordinal()) {
                gamer.setVanish(true);

                player.setGameMode(GameMode.CREATIVE);

                player.sendMessage("§dVocê entrou no modo VANISH.");
                player.sendMessage("§dAgora você está invisível para todos sem OP.");
            } else
                gamer.setAlive(false);

            gamer.setState(GamerState.DEAD);

            List<ItemStack> drops = new ArrayList<>(event.getDrops());

            drops.forEach(item -> {
                if (gamer.getKit() != null && item != null && Main.getInstance().getKitManager().isKitItem(gamer.getKit(), item)) {
                    event.getDrops().remove(item);
                }
            });

            StringBuilder sb = new StringBuilder();

            if (player.getKiller() == null) {
                sb.append("§b").append(player.getName()).append("(").append(gamer.getKitName()).append(") morreu sozinho.");
            } else {
                Gamer killer = Gamer.getGamer(player.getKiller().getUniqueId());
                killer.addKill();

                sb.append("§b").append(player.getKiller().getName()).append("(").append(killer.getKitName()).append(") matou ").append(player.getName()).append("(").append(gamer.getKitName()).append(")");
                String item = getItem(player.getKiller().getItemInHand());
                if (item != null)
                    sb.append(" usando ").append(item);
            }

            sb.append("\n§e").append(Main.getInstance().getGamerProvider().getAliveGamers().size()).append(" jogadores restantes.");

            if (gamer.isOnline()) {
                event.setDeathMessage(sb.toString());
            } else
                event.setDeathMessage(null);
        } else
            event.setDeathMessage(null);

        Main.getInstance().getGamerProvider().getAliveGamers().forEach(g -> {
            if (g.getPlayer() != null && !g.isSpectators()) {
                g.getPlayer().hidePlayer(player);
            }
        });
    }

    @EventHandler
    public void onUpdateInvisibility(TimeSecondEvent event) {
        Main.getInstance().getGameManager().getDeadPlayers().forEach(gamer -> {
            if (!gamer.isVanish() && gamer.getPlayer() != null && !gamer.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 2));
            }
        });
    }

    private String getItem(ItemStack stack) {
        switch (stack.getType()) {
            case WOOD_SWORD:
                return "Espada de Madeira";
            case STONE_SWORD:
                return "Espada de Pedra";
            case IRON_SWORD:
                return "Espada de Ferro";
            case DIAMOND_SWORD:
                return "Espada de Diamante";
            case WOOD_AXE:
                return "Machado de Madeira";
            case STONE_AXE:
                return "Machado de Pedra";
            case IRON_AXE:
                return "Machado de Ferro";
            case DIAMOND_AXE:
                return "Machado de Diamante";
            case BOWL:
                return "Pote";
            case AIR:
                return "Mão";
            default:
                return null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPvPMonitor(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player && e.getEntity() != e.getDamager()) {
            if (!e.isCancelled()) {
                Gamer c1 = Gamer.getGamer(e.getEntity().getUniqueId());
                Gamer c2 = Gamer.getGamer(e.getDamager().getUniqueId());
                c1.setLastCombat(c2);
                c1.setFighting(8);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());
        if (Main.getInstance().getKitManager().isKitItem(gamer.getKit(), event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (event.getEntity() != null)
                event.getEntity().remove();
        }, 90 * 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakMonitor(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        if (!Variable.BUILD.isActive() && event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE && !gamer.isVanish())
            event.setCancelled(true);

        if (Gamer.getGamer(event.getPlayer().getUniqueId()).isSpectators()) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();

        if (!Main.getInstance().getGameManager().isStagePregame()) {
            if (block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.RED_MUSHROOM) {
                Collection<ItemStack> drops = event.getBlock().getDrops(player.getItemInHand());

                block.setType(Material.AIR);

                for (ItemStack stack : drops)
                    gamer.add(player.getInventory(), stack.getType(), block.getLocation(), stack);
            } else if (block.getType() == Material.COCOA) {
                /*Collection<ItemStack> drops = event.getBlock().getDrops(player.getItemInHand());

                block.setType(Material.AIR);

                for (ItemStack stack : drops)
                    gamer.add(player.getInventory(), stack.getType(), block.getLocation(), stack);*/
                CocoaPlant cocoa = (CocoaPlant) block.getState().getData();

                if (cocoa.getSize() == CocoaPlant.CocoaPlantSize.SMALL) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);

                    gamer.add(player.getInventory(), Material.getMaterial(351), block.getLocation(), new ItemStack(Material.getMaterial(351), 1, (byte) 3));
                } else if (cocoa.getSize() == CocoaPlant.CocoaPlantSize.MEDIUM) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);

                    gamer.add(player.getInventory(), Material.getMaterial(351), block.getLocation(), new ItemStack(Material.getMaterial(351), 2, (byte) 3));
                } else if (cocoa.getSize() == CocoaPlant.CocoaPlantSize.LARGE) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);

                    gamer.add(player.getInventory(), Material.getMaterial(351), block.getLocation(), new ItemStack(Material.getMaterial(351), 3, (byte) 3));
                }
            } else if (block.getType() == Material.STONE) {
                Collection<ItemStack> drops = event.getBlock().getDrops(player.getItemInHand());

                block.setType(Material.AIR);

                for (ItemStack stack : drops)
                    gamer.add(player.getInventory(), stack.getType(), block.getLocation(), stack);
            } else if (block.getType().name().contains("LOG")) {
                Collection<ItemStack> drops = event.getBlock().getDrops(player.getItemInHand());

                block.setType(Material.AIR);

                for (ItemStack stack : drops)
                    gamer.add(player.getInventory(), stack.getType(), block.getLocation(), stack);
            }
        }
    }
}