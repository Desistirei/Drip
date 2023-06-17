package br.com.stenox.cxc.kit.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Gladiator extends Kit {

    public static HashMap<Player, Player> gladiatorA = new HashMap<>();
    public static HashMap<Player, Location> oldLoc = new HashMap<>();
    public static List<Block> arena = new ArrayList<Block>();

    public Gladiator(GameManager manager) {
        super(manager);

        setIcon(new ItemStack(Material.IRON_FENCE));
        setDescription("§7Duele com seu adversário nas alturas.");
        setItems(createItemStack("§aGladiator", Material.IRON_FENCE));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            Player p2 = (Player) e.getEntity();
            if (gladiatorA.containsKey(p2) && gladiatorA.get(p2) != p) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (e.getBlock().getType() == Material.GLASS) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Player gladiator = event.getPlayer();
        double xx = gladiator.getLocation().getX();
        double zz = gladiator.getLocation().getZ();
        if (event.getRightClicked() instanceof Player) {
            final Player desafiado = (Player) event.getRightClicked();
            if (hasKit(gladiator)) {
                if (!isKitItem(gladiator.getItemInHand(), Material.IRON_FENCE, "§aGladiator")) {
                    return;
                }
                if (getManager().isStageInvincibility()) {
                    gladiator.sendMessage("§cVocê não pode utilizar o kit Gladiator na invencibilidade.");
                    return;
                }
                if (inCooldown(gladiator)) {
                    sendCooldown(gladiator);
                    return;
                }

                if (Gamer.getGamer(desafiado.getUniqueId()).getKit().getName().equalsIgnoreCase("Neo")) {
                    gladiator.sendMessage("§cVocê não pode puxar jogadores utilizando o kit Neo para uma batalha.");
                    return;
                }
                if (getManager().isStagePlaying() && getManager().getGameTime() >= 2100 && Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() < 2405) {
                    gladiator.sendMessage("§cVocê não pode utilizar o kit Gladiator antes da arena final nascer.");
                    return;
                }

                if (!gladiatorA.containsKey(gladiator) && !gladiatorA.containsKey(desafiado) && !getGamer(desafiado).isSpectators() && !getGamer(gladiator).isSpectators()) {
                    if (!canCreate(xx, zz)) {
                        xx = getRandomInteger(360);
                        zz = getRandomInteger(360);
                        if (!canCreate(xx, zz)) {
                            xx = getRandomInteger(360);
                            zz = getRandomInteger(360);
                        }
                        if (!canCreate(xx, zz)) {
                            gladiator.sendMessage("§cNão foi possível criar a arena neste local.");
                            return;
                        }
                    }

                    if (gladiator.getLocation().getBlockX() > 390 || gladiator.getLocation().getBlockX() <= -390 || gladiator.getLocation().getBlockZ() >= 390 || gladiator.getLocation().getBlockZ() <= -390) {
                        gladiator.sendMessage("§cVocê não pode usar o kit Gladiator próximo a borda do mundo.");
                        return;
                    }

                    Location alto = new Location(gladiator.getWorld(), xx, 129.0d, zz);
                    for (int x = -8; x <= 7; x++) {
                        for (int z = -8; z <= 7; z++) {
                            Block chao = alto.clone().add(x, 0.0d, z).getBlock();
                            chao.setType(Material.GLASS);
                            Gladiator.arena.add(chao);
                            if (x == -8 || x == 7) {
                                for (int y = 0; y <= 13; y++) {
                                    Block paredeX = alto.clone().add(x, y, z).getBlock();
                                    paredeX.setType(Material.GLASS);
                                    Gladiator.arena.add(paredeX);
                                }
                            }
                            if (z == -8 || z == 7) {
                                for (int y2 = 0; y2 <= 13; y2++) {
                                    Block paredeZ = alto.clone().add(x, y2, z).getBlock();
                                    paredeZ.setType(Material.GLASS);
                                    Gladiator.arena.add(paredeZ);
                                }
                            }
                        }
                    }
                    if (desafiado.isInsideVehicle()) {
                        desafiado.leaveVehicle();
                        if (desafiado.isInsideVehicle()) {
                            desafiado.getVehicle().eject();
                        }
                    }
                    oldLoc.put(gladiator, gladiator.getLocation().clone());
                    oldLoc.put(desafiado, desafiado.getLocation().clone());
                    gladiatorA.put(gladiator, desafiado);
                    gladiatorA.put(desafiado, gladiator);
                    Location glad = alto.clone().add(-5.5, 1.2D, -5.5);
                    Location desa = alto.clone().add(5.5, 1D, 5.5);
                    desa.setYaw(130);
                    glad.setYaw(-45);
                    gladiator.teleport(glad);
                    desafiado.teleport(desa);

                    Ninja.target.remove(desafiado.getUniqueId());
                    Ninja.target.remove(gladiator.getUniqueId());

                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255), false);
                    desafiado.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255), false);
                    new BukkitRunnable() {
                        int Remaining = 180;

                        public void run() {
                            this.Remaining--;
                            if (!Gladiator.gladiatorA.containsKey(gladiator)
                                    || !Gladiator.gladiatorA.containsKey(desafiado) || !gladiator.isOnline()
                                    || !desafiado.isOnline()) {
                                for (int x = -8; x <= 7; x++) {
                                    for (int z = -8; z <= 7; z++) {
                                        for (int y = 0; y <= 14; y++) {
                                            Location blocks = alto.clone().add((double) x, (double) y, (double) z);
                                            Gladiator.arena.remove(blocks.getBlock());
                                            blocks.getBlock().setType(Material.AIR);
                                            cancel();
                                        }
                                    }
                                }
                                if (Gladiator.gladiatorA.containsKey(gladiator) && gladiator.isOnline()) {
                                    Gladiator.gladiatorA.remove(gladiator);
                                    gladiator.teleport(Gladiator.oldLoc.get(gladiator));
                                } else if (Gladiator.gladiatorA.containsKey(desafiado) && desafiado.isOnline()) {
                                    Gladiator.gladiatorA.remove(desafiado);
                                    desafiado.teleport(Gladiator.oldLoc.get(desafiado));
                                }
                                if (gladiator.isOnline() && Gladiator.oldLoc.get(gladiator) != null) {
                                    gladiator.teleport(Gladiator.oldLoc.get(gladiator));
                                }
                            } else if (this.Remaining == 90) {
                                gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (60 * 20), 3));
                                desafiado.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (60 * 20), 3));
                            } else if (this.Remaining == 0) {
                                gladiator.removePotionEffect(PotionEffectType.WITHER);
                                desafiado.removePotionEffect(PotionEffectType.WITHER);
                                if (Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() >= ((int)Variable.TIMINGS_FINAL_ARENA.getValue() + 601)) {
                                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (60 * 20), 2), true);
                                    desafiado.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (60 * 20), 2), true);
                                }
                                desafiado.addPotionEffect(
                                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255), true);
                                gladiator.addPotionEffect(
                                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255), true);
                                gladiator.teleport(Gladiator.oldLoc.get(gladiator));
                                desafiado.teleport(Gladiator.oldLoc.get(desafiado));
                                if (hasKit(gladiator))
                                    addCooldown(gladiator, 4);
                                Gladiator.oldLoc.remove(gladiator);
                                Gladiator.oldLoc.remove(desafiado);
                                Gladiator.gladiatorA.remove(gladiator);
                                Gladiator.gladiatorA.remove(desafiado);
                                for (int x2 = -8; x2 <= 7; x2++) {
                                    for (int z2 = -8; z2 <= 7; z2++) {
                                        for (int y2 = 0; y2 <= 13; y2++) {
                                            Location blocks2 = alto.clone().add((double) x2, (double) y2, (double) z2);
                                            Gladiator.arena.remove(blocks2.getBlock());
                                            if (blocks2.getBlock().getData() != 0) {
                                                blocks2.getBlock().setData((byte) 0);
                                            }
                                            blocks2.getBlock().setType(Material.AIR);
                                            cancel();
                                        }
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (gladiatorA.containsKey(event.getPlayer()) && event.getBlock().getType().toString().contains("FENCE")) {
            event.setCancelled(true);
        }
            for (Location locations : oldLoc.values()) {
                if (locations.distance(event.getBlock().getLocation()) <= 3) {
                    event.getPlayer().sendMessage("§cVocê não pode colocar blocos aqui pois um jogador foi desafiado para uma luta Gladiator anteriormente nesta localização.");
                    event.setCancelled(true);
                }
            }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        if (arena.contains(e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (gladiatorA.containsKey(p) && oldLoc.containsKey(p)) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            Player outro = gladiatorA.get(p);
            outro.teleport(oldLoc.get(outro));
            p.removePotionEffect(PotionEffectType.WITHER);
            outro.removePotionEffect(PotionEffectType.WITHER);
            if (Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() >= ((int) Variable.TIMINGS_FINAL_ARENA.getValue() + 601)) {
                outro.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);
            }
            Gamer account = Gamer.getGamer(outro.getUniqueId());

            account.addKill();

            gladiatorA.remove(outro);
            gladiatorA.remove(p);
            oldLoc.remove(outro);
            oldLoc.remove(p);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onGamerDeath(PlayerDeathEvent event) {
        Player morreu = event.getEntity();
        if (gladiatorA.containsKey(morreu)) {
            Player matou = gladiatorA.get(morreu);
            if (gladiatorA.containsKey(morreu) && gladiatorA.containsKey(matou) && oldLoc.containsKey(morreu) && oldLoc.containsKey(matou) && gladiatorA.get(morreu) == matou && gladiatorA.get(matou) == morreu) {

                matou.teleport(oldLoc.get(matou));
                matou.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255), true);
                matou.removePotionEffect(PotionEffectType.WITHER);
                morreu.removePotionEffect(PotionEffectType.WITHER);

                if (Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() >= ((int) Variable.TIMINGS_FINAL_ARENA.getValue() + 601))
                    matou.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);

                List<ItemStack> drops = new ArrayList<>();

                for (ItemStack drop : event.getDrops()) {
                    if (drop == null) continue;
                    if (Main.getInstance().getKitManager().isKitItem(Gamer.getGamer(morreu.getUniqueId()).getKit(), drop)) continue;
                    drops.add(drop);
                }

                event.getDrops().clear();

                drops.forEach(drop -> {
                    getManager().getWorld().dropItemNaturally(oldLoc.get(morreu), drop);
                });

                drops.clear();
                oldLoc.remove(matou);
                oldLoc.remove(morreu);
                gladiatorA.remove(morreu);
                gladiatorA.remove(matou);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceIgnore(BlockPlaceEvent event) {
        if (hasKit(event.getPlayer()) && isKitItem(event.getPlayer().getItemInHand(), Material.IRON_FENCE, "§aGladiator")) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
        if (gladiatorA.containsKey(event.getPlayer())) {
            Gladiator.arena.add(event.getBlock());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Gladiator.arena.contains(event.getBlock()) && !gladiatorA.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (arena.contains(event.getBlock()) && event.getBlock().getType() == Material.GLASS) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block b : event.blockList()) {
            if (Gladiator.arena.contains(b)) {
                event.setCancelled(true);
                if (b.getType() == Material.GLASS) {
                    b.setType(Material.GLASS);
                    b.setData((byte) 1);
                } else if (b.getType() == Material.GLASS) {
                    if (b.getData() == 1) {
                        b.setData((byte) 4);
                    } else if (b.getData() == 4) {
                        b.setData((byte) 14);
                    }
                }
                if (b.getType() == Material.NETHERRACK) {
                    b.setType(Material.SOUL_SAND);
                    b.setData((byte) 0);
                } else if (b.getType() == Material.SOUL_SAND) {
                    b.setType(Material.NETHER_BRICK);
                    b.setData((byte) 0);
                }
            }
        }
    }

    @EventHandler
    public void onVehicle(VehicleEnterEvent e) {
        if (e.getEntered() instanceof Player) {
            Player p = (Player) e.getEntered();
            if (gladiatorA.containsKey(p))
                e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = (Player) e.getPlayer();
        if (Gladiator.gladiatorA.containsKey(p) && p.getLocation().getY() >= 142.5) {
            Player pp = (Player) gladiatorA.get(p);
            p.setFallDistance(-p.getFallDistance());
            pp.setFallDistance(-p.getFallDistance());
            pp.teleport(oldLoc.get(pp));
            p.teleport(oldLoc.get(p));
            oldLoc.remove(p);
            oldLoc.remove(pp);
            gladiatorA.remove(pp);
            gladiatorA.remove(p);
            p.removePotionEffect(PotionEffectType.WITHER);
            pp.removePotionEffect(PotionEffectType.WITHER);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255), false);
            pp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255), false);
            if (Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() >= ((int) Variable.TIMINGS_FINAL_ARENA.getValue() + 601)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);
                pp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);
            }
            if (hasKit(p))
                addCooldown(p, 4);
            if (hasKit(pp))
                addCooldown(pp, 4);
        } else if (Gladiator.gladiatorA.containsKey(p) && p.getLocation().getY() < 128) {
            Player pp = (Player) gladiatorA.get(p);
            p.setFallDistance(-p.getFallDistance());
            pp.setFallDistance(-p.getFallDistance());
            pp.teleport(oldLoc.get(pp));
            p.teleport(oldLoc.get(p));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255), true);
            pp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255), true);
            if (Variable.FINAL_BATTLE.isActive() && getManager().getGameTime() >= ((int) Variable.TIMINGS_FINAL_ARENA.getValue() + 601)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);
                pp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (20 * 300), 2), true);
            }
            oldLoc.remove(p);
            oldLoc.remove(pp);
            gladiatorA.remove(pp);
            gladiatorA.remove(p);
            p.removePotionEffect(PotionEffectType.WITHER);
            pp.removePotionEffect(PotionEffectType.WITHER);
            if (hasKit(p))
                addCooldown(p, 4);
            if (hasKit(pp))
                addCooldown(pp, 4);
        }

    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            e.getPlayer().teleport(e.getTo());
        }
    }

    public boolean canCreate(double xx, double zz) {
        Location arena = new Location(getManager().getWorld(), xx, 129, zz);
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                Block b = arena.clone().add(x, 0, z).getBlock();
                if (b.getType() != Material.AIR) {
                    return false;
                }
            }
        }
        return true;
    }

    public Integer getRandomInteger(int bound) {
        boolean a = new Random().nextBoolean();
        int ai = new Random().nextInt(bound);
        return ((a ? -ai : ai));
    }
}