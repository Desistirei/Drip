package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.special.SpecialKit;
import br.com.stenox.cxc.utils.ActionBarAPI;
import br.com.stenox.cxc.utils.Items;
import br.com.stenox.cxc.utils.LongManager;
import br.com.stenox.cxc.utils.scoreboard.ScoreboardWrapper;
import br.com.stenox.cxc.utils.scoreboard.sidebar.Sidebar;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

        Player player = event.getPlayer();

        Gamer gamer = Gamer.getGamer(player.getUniqueId());
        if (gamer == null) {
            if (Main.getInstance().getGamerRepository().contains(player.getUniqueId()))
                gamer = Main.getInstance().getGamerRepository().fetch(player.getUniqueId());
            else {
                gamer = new Gamer(player.getUniqueId());
                gamer.setName(player.getName());
            }
            if (gamer.getName().equalsIgnoreCase("stenoxpvp"))
                gamer.setGroup(Group.YOUTUBER, -1);

            Main.getInstance().getGamerProvider().create(gamer);
        }

        long groupTime = (gamer.getGroupTime() / 1000) - (System.currentTimeMillis() / 1000);

        if (gamer.getGroupTime() != -1)
            if (groupTime <= 0)
                gamer.setGroup(Group.MEMBER, -1);


        if (gamer.isBanned() && !gamer.getName().equalsIgnoreCase("stenoxpvp")) {
            long banTime = (gamer.getBanTime() / 1000) - (System.currentTimeMillis() / 1000);
            if (gamer.getBanTime() != -1 && banTime != -1 && banTime <= 0) {
                gamer.unban();
                return;
            }
            gamer.setAlive(false);

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cVocê está banido do servidor. \n \n §cMotivo: " + gamer.getBanReason() + " \n Expira em: " + LongManager.formatLong(gamer.getBanTime()));

            Main.getInstance().getGamerProvider().remove(player.getUniqueId());
            return;
        }

        if (Main.getInstance().getGame().getStage() != GameStage.STARTING) {
            if (gamer.isAlive() && gamer.isDisconnect()) {
                event.allow();
                return;
            }

            if (Main.getInstance().getGame().getStage() == GameStage.ENDING) {
                gamer.setAlive(false);
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cA partida está acabando...");
                return;
            }

            if (!player.isOp() && gamer.getGroup() == Group.MEMBER) {
                gamer.setAlive(false);
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO jogo já começou \n Apenas jogadores com VIP podem entrar agora.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        event.setJoinMessage(null);

        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        gamer.setOnline(true);

        gamer.setTag(gamer.getGroup().getTag());
        gamer.setIp(player.getAddress().getAddress().toString());

        ScoreboardWrapper wrapper = new ScoreboardWrapper(player.getScoreboard());
        Sidebar sidebar = wrapper.getSidebar();
        sidebar.setTitle(Main.SCOREBOARD_TITLE);

        gamer.setWrapper(wrapper);

        if (Main.getInstance().getGame().getStage() == GameStage.STARTING) {
            for (int i = 0; i < 100; i++)
                player.sendMessage("");

            player.setMaxHealth(20.0D);
            player.setHealth(20.0D);
            player.setSaturation(20);
            player.getInventory().clear();
            player.teleport(Main.SPAWN);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setLevel(0);
            player.setExp(0);
            player.setGameMode(GameMode.SURVIVAL);

            Items.SPAWN.build(player);
            if (Main.getInstance().getGame().getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN)
                Items.CXC.build(player);

        } else if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
            if (Main.getInstance().getGame().getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
                if (gamer.isAlive() && gamer.isDisconnect()) {
                    event.setJoinMessage("§7" + player.getName() + " entrou na partida.");
                    gamer.setDisconnect(false);
                    return;
                }

                player.setMaxHealth(20.0D);
                player.setHealth(20.0D);
                player.setSaturation(20);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.teleport(Main.SPAWN);
                player.setAllowFlight(true);
                player.setLevel(0);
                player.setExp(0);
                player.setGameMode(GameMode.SPECTATOR);

                gamer.setAlive(false);

                Items.DEAD.build(player);
            } else {
                if (gamer.isAlive() && gamer.isDisconnect()) {
                    event.setJoinMessage("§7" + player.getName() + " entrou na partida.");
                    gamer.setDisconnect(false);
                    return;
                }

                if (Variable.RESPAWN.isActive()) {
                    event.setJoinMessage("§7" + player.getName() + " entrou na partida.");

                    player.setHealth(player.getMaxHealth());
                    player.setSaturation(20.0F);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);

                    SpecialKit defaultKit = SpecialKit.getKit("default");
                    if (defaultKit != null) {
                        player.getInventory().setContents(defaultKit.getContents());
                        player.getInventory().setArmorContents(defaultKit.getArmorContents());
                    } else
                        Items.INVINCIBILITY.build(player);

                    int x = new Random().nextInt(300);
                    int z = new Random().nextInt(300);

                    player.teleport(new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt(x, z), z));

                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.setLevel(0);
                    player.setExp(0);
                    player.setGameMode(GameMode.SURVIVAL);

                    gamer.setAlive(true);
                } else {
                    player.setMaxHealth(20.0D);
                    player.setHealth(20.0D);
                    player.setSaturation(20);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.teleport(Main.SPAWN);
                    player.setAllowFlight(true);
                    player.setLevel(0);
                    player.setExp(0);
                    player.setGameMode(GameMode.SPECTATOR);

                    gamer.setAlive(false);

                    Items.DEAD.build(player);
                }
            }
        } else if (Main.getInstance().getGame().getStage() == GameStage.IN_GAME) {
            if (Main.getInstance().getGame().getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
                if (gamer.isDisconnect() && gamer.isAlive()) {
                    gamer.setDisconnect(false);
                    event.setJoinMessage("§7" + event.getPlayer().getName() + " entrou na partida.");
                    return;
                }

                player.setMaxHealth(20.0D);
                player.setHealth(20.0D);
                player.setSaturation(20);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.teleport(Main.SPAWN);
                player.setAllowFlight(true);
                player.setLevel(0);
                player.setExp(0);
                player.setGameMode(GameMode.SPECTATOR);

                gamer.setAlive(false);

                Items.DEAD.build(player);
            } else {
                if (gamer.isDisconnect() && gamer.isAlive()) {
                    gamer.setDisconnect(false);
                    event.setJoinMessage("§7" + event.getPlayer().getName() + " entrou na partida.");
                    return;
                }
                if (Main.getInstance().getGame().getTime() < 300) {
                    player.setHealth(player.getMaxHealth());
                    player.setSaturation(20.0F);
                    player.getInventory().clear();

                    int x = new Random().nextInt(300);
                    int z = new Random().nextInt(300);

                    player.teleport(new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt(x, z), z));

                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.setLevel(0);
                    player.setExp(0.0F);
                    player.setGameMode(GameMode.SURVIVAL);

                    gamer.setAlive(true);

                    Items.IN_GAME.build(player);
                } else {
                    player.setHealth(player.getMaxHealth());
                    player.setSaturation(20.0F);
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);

                    gamer.setAlive(false);

                    Items.DEAD.build(player);
                }
            }
        }

        Main.getInstance().getGamerProvider().updatePlayers();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());

        Main.getInstance().getGamerProvider().update(gamer);

        if (gamer != null) {
            gamer.setOnline(false);

            if (Main.getInstance().getGame().getStage() == GameStage.STARTING) {
                event.getPlayer().performCommand("reset nick");
                Main.getInstance().getGamerProvider().remove(gamer.getUniqueId());
            } else {
                if (gamer.isAlive()) {
                    if (gamer.isFighting()) {
                        /*List<ItemStack> drop = new ArrayList<>();
                        drop.addAll(Arrays.asList(event.getPlayer().getInventory().getContents()));
                        drop.addAll(Arrays.asList(event.getPlayer().getInventory().getArmorContents()));

                        Location loc = event.getPlayer().getLocation();
                        if (gamer.getLastCombat() != null && gamer.getLastCombat().isAlive()) {
                            loc = gamer.getLastCombat().getPlayer().getLocation();
                        }

                        for (ItemStack stack : drop) {
                            if (stack == null || stack.getType().equals(Material.AIR))
                                continue;
                            event.getPlayer().getWorld().dropItemNaturally(loc.clone().add(0, 0.5, 0), stack);
                        }

                        gamer.setAlive(false);
                        Bukkit.broadcastMessage("§b" + event.getPlayer().getName() + " saiu do servidor durante um combate e foi desclassificado.");
                        Bukkit.broadcastMessage("§e" + Main.getInstance().getGamerProvider().getAliveGamers().size() + " jogador" + (Main.getInstance().getGamerProvider().getAliveGamers().size() == 1 ? "" : "es") + " restante" + (Main.getInstance().getGamerProvider().getAliveGamers().size() == 1 ? "." : "s."));
                        if (gamer.getLastCombat() != null) {
                            if (gamer.getLastCombat().isAlive()) {
                                gamer.getLastCombat().addKill();
                                if (gamer.getLastCombat().getPlayer() != null)
                                    gamer.getLastCombat().getPlayer().playSound(gamer.getPlayer().getLocation(), Sound.ORB_PICKUP, 4f, 4f);
                            }
                        }*/

                        event.getPlayer().setHealth(0.0D);

                        Bukkit.broadcastMessage("§b" + event.getPlayer().getName() + " saiu do servidor durante um combate e foi desclassificado.");
                        Bukkit.broadcastMessage("§e" + Main.getInstance().getGamerProvider().getAliveGamers().size() + " jogador" + (Main.getInstance().getGamerProvider().getAliveGamers().size() == 1 ? "" : "es") + " restante" + (Main.getInstance().getGamerProvider().getAliveGamers().size() == 1 ? "." : "s."));
                    } else {
                        Bukkit.broadcastMessage("§7" + event.getPlayer().getName() + " saiu do servidor.");

                        List<ItemStack> drop = new ArrayList<>();

                        drop.addAll(Arrays.asList(event.getPlayer().getInventory().getContents()));
                        drop.addAll(Arrays.asList(event.getPlayer().getInventory().getArmorContents()));

                        Location locToDrop = event.getPlayer().getLocation();

                        gamer.setDisconnect(true);

                        new BukkitRunnable() {
                            public void run() {
                                Gamer gamer = Gamer.getGamer(event.getPlayer().getUniqueId());

                                if (gamer.isAlive() && gamer.isDisconnect()) {
                                    gamer.setAlive(false);
                                    gamer.setDisconnect(false);

                                    Bukkit.broadcastMessage("§b" + event.getPlayer().getName() + " não voltou a partida e foi eliminado.");
                                    Bukkit.broadcastMessage("§e" + (Main.getInstance().getGamerProvider().getAliveGamers().size() - 1 + " jogador" + (Main.getInstance().getGamerProvider().getAliveGamers().size() - 1 == 1 ? "" : "es") + " restante" + ((Main.getInstance().getGamerProvider().getAliveGamers().size() - 1 == 1) ? "." : "s.")));

                                    for (ItemStack i : drop) {
                                        if (i == null)
                                            continue;
                                        if (i.getType() == Material.AIR)
                                            continue;
                                        locToDrop.getWorld().dropItemNaturally(locToDrop.clone().add(0.5, 0.5, 0.5), i);
                                    }
                                }
                            }
                        }.runTaskLater(Main.getInstance(), 20L * 60);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.MUSHROOM_SOUP) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        double beforeHealth = player.getHealth();

        if (beforeHealth < player.getMaxHealth()) {
            if ((beforeHealth + 7) > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
                if (player.getFoodLevel() < 20) {
                    int i = ((toInt(beforeHealth) + 7) - toInt(player.getMaxHealth()));
                    player.setFoodLevel((Math.min(player.getFoodLevel() + i, 20)));
                    player.setSaturation(3);
                }
            } else {
                player.setHealth(player.getHealth() + 7);
            }
            ActionBarAPI.send(player, "§c+3,5 §4❤");
            player.setItemInHand(new ItemStack(Material.BOWL));
        } else if (player.getFoodLevel() < 20) {
            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 7);
            player.setSaturation(3);
            player.setItemInHand(new ItemStack(Material.BOWL));
        }
    }

    private int toInt(Double d) {
        return d.intValue();
    }
}
