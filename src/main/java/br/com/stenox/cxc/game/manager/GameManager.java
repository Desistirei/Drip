package br.com.stenox.cxc.game.manager;

import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import br.com.stenox.cxc.modifier.WorldModifier;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.kit.special.SpecialKit;
import br.com.stenox.cxc.gamer.provider.GamerProvider;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GameManager {

    private final GamerProvider gamerProvider;

    @Getter
    private final WorldModifier worldModifier;

    public GameManager(GamerProvider gamerProvider) {
        this.gamerProvider = gamerProvider;
        this.worldModifier = new WorldModifier();
    }

    public Set<Gamer> getAlivePlayers() {
        return gamerProvider.getAliveGamers();
    }

    public List<Gamer> getDeadPlayers() {
        return gamerProvider.getGamers().stream().filter(gamer -> !gamer.isAlive() || gamer.isVanish()).collect(Collectors.toList());
    }

    public void giveItems() {
        SpecialKit defaultKit = SpecialKit.getKit("default");
        if (defaultKit != null)
            gamerProvider.getGamers().forEach(gamer -> {
                gamer.getPlayer().getInventory().clear();
                gamer.getPlayer().closeInventory();
                gamer.getPlayer().getInventory().setContents(defaultKit.getContents());
                gamer.getPlayer().getInventory().setArmorContents(defaultKit.getArmorContents());
                if (gamer.getKit() != null)
                    gamer.getKit().give(gamer.getPlayer());
                gamer.getPlayer().updateInventory();
            });
        else
            gamerProvider.getGamers().forEach(gamer -> Main.getInstance().getKitManager().giveItems(gamer));
    }

    public void giveItems(Gamer gamer) {
        SpecialKit defaultKit = SpecialKit.getKit("default");
        if (defaultKit != null) {
            gamer.getPlayer().getInventory().clear();
            gamer.getPlayer().closeInventory();
            gamer.getPlayer().getInventory().setContents(defaultKit.getContents());
            gamer.getPlayer().getInventory().setArmorContents(defaultKit.getArmorContents());
            if (gamer.getKit() != null)
                gamer.getKit().give(gamer.getPlayer());
            gamer.getPlayer().updateInventory();
        } else {
            Main.getInstance().getKitManager().giveItems(gamer);
        }
    }

    public void checkWin() {
        if (Main.getInstance().getGame().getStage() == GameStage.ENDING)
            return;
        if (Main.getInstance().getGame().getMode() != br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
            if (Main.getInstance().getGameManager().getAlivePlayers().size() == 1) {
                Gamer gamer = Main.getInstance().getGameManager().getAlivePlayers().stream().findFirst().orElse(null);
                if (gamer == null) return;

                if (gamer.getPlayer().isOnline()) {
                    makeWin(gamer.getPlayer());
                }
            } else if (Main.getInstance().getGameManager().getAlivePlayers().size() == 0) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), Bukkit::shutdown, 50L);
            }
        } else {
            if (Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.RED) == 0 && Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.BLUE) != 0) {
                makeWin(GamerTeam.BLUE);
            } else if (Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.BLUE) == 0 && Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.RED) != 0) {
                makeWin(GamerTeam.RED);
            } else if (Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.RED) == 0 && Main.getInstance().getGamerProvider().getPlayersContForTeam(GamerTeam.BLUE) == 0) {
                Bukkit.shutdown();
            }
        }
    }

    public void makeWin(Player player) {
        Main.getInstance().getGame().setStage(GameStage.ENDING);
        Bukkit.getWorlds().get(0).getEntities().stream().filter(entity -> entity instanceof Item).forEach(Entity::remove);
        Location cake = player.getLocation().clone();
        cake.setY((cake.getWorld().getHighestBlockYAt(cake) + 40));
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                cake.clone().add(x, 0.0D, z).getBlock().setType(Material.GLASS);
                cake.clone().add(x, 1.0D, z).getBlock().setType(Material.CAKE_BLOCK);
            }
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(null);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.teleport(cake.clone().add(0.0D, 4.0D, 0.0D)), 3L);
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        CompletableFuture.runAsync(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(cake.clone().add(0.0D, 4.0D, 0.0D));
            }
        });

        player.getInventory().setHeldItemSlot(0);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 254), true);

        player.getInventory().setItem(0, new ItemCreator(Material.MAP).setName("§a" + player.getName() + ", vencedor!").getStack());
        player.getInventory().setItem(1, new ItemCreator(Material.WATER_BUCKET).setName("§aMLG").getStack());

        player.updateInventory();

        startFirework(player, player.getLocation(), new Random());

        Bukkit.getWorld("world").setTime(13000L);
    }

    public void makeWin(GamerTeam team) {
        Main.getInstance().getGame().setStage(GameStage.ENDING);

        Location cake = new Location(Main.getInstance().getGameManager().getWorld(), 0, 110, 0);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                cake.clone().add(x, 0.0D, z).getBlock().setType(Material.GLASS);
                cake.clone().add(x, 1.0D, z).getBlock().setType(Material.CAKE_BLOCK);
            }
        }
        startFirework(team, cake, new Random());
        List<Gamer> gamers = team.getAlivePlayers();

        for (Gamer gamer : gamers) {
            Player player = gamer.getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setFireTicks(0);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.getInventory().setHeldItemSlot(4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 254), true);
            player.getInventory().setItem(4, new ItemCreator(Material.WATER_BUCKET).setName("§aMLG").getStack());
            player.updateInventory();
        }

        Bukkit.getOnlinePlayers().forEach(online -> online.teleport(cake.clone().add(0.0D, 4.0D, 0.0D)));
    }

    public void startFirework(final Player player, Location location, Random random) {
        for (int i = 0; i < 5; i++) {
            spawnRandomFirework(location.add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
        }
        new BukkitRunnable() {
            public void run() {
                spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, 10.0D));
                spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, 10.0D));
                spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, 5.0D));
                spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, -5.0D));
                spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, 5.0D));
                spawnRandomFirework(player.getLocation().add(1.0D, 0.0D, 9.0D));
                Bukkit.broadcastMessage("§c" + player.getName() + " venceu o torneio!");
            }
        }.runTaskTimer(Main.getInstance(), 10L, 30L);
    }

    public void startFirework(GamerTeam team, Location location, Random random) {
        for (int i = 0; i < 5; i++) {
            spawnRandomFirework(location.clone().add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
        }
        new BukkitRunnable() {
            public void run() {
                spawnRandomFirework(location.clone().add(-10.0D, 0.0D, 10.0D));
                spawnRandomFirework(location.clone().add(10.0D, 0.0D, 10.0D));
                spawnRandomFirework(location.clone().add(-5.0D, 0.0D, 5.0D));
                spawnRandomFirework(location.clone().add(5.0D, 0.0D, -5.0D));
                spawnRandomFirework(location.clone().add(5.0D, 0.0D, 5.0D));
                spawnRandomFirework(location.clone().add(1.0D, 0.0D, 9.0D));
                Bukkit.broadcastMessage("§cO time " + team.getColor() + team.getName() + "§c venceu o torneio!");
            }
        }.runTaskTimer(Main.getInstance(), 10L, 30L);
    }

    public void spawnRandomFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();

        int rt = new Random().nextInt(4) + 1;

        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (rt == 1) {
            type = FireworkEffect.Type.BALL;
        } else if (rt == 2) {
            type = FireworkEffect.Type.BALL_LARGE;
        } else if (rt == 3) {
            type = FireworkEffect.Type.BURST;
        } else if (rt == 4) {
            type = FireworkEffect.Type.STAR;
        } else if (rt == 5) {
            type = FireworkEffect.Type.CREEPER;
        }
        FireworkEffect effect = FireworkEffect.builder().flicker(new Random().nextBoolean()).withColor(Color.WHITE).withColor(Color.ORANGE).withFade(Color.FUCHSIA).with(type).trail(new Random().nextBoolean()).build();
        fwm.addEffect(effect);
        fwm.setPower(new Random().nextInt(2) + 1);
        firework.setFireworkMeta(fwm);
    }

    public World getWorld() {
        return Bukkit.getWorld("world");
    }

    public boolean isStagePregame(){
        return Main.getInstance().getGame().getStage() == GameStage.STARTING;
    }

    public boolean isStageInvincibility(){
        return Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY;
    }

    public boolean isStagePlaying(){
        return Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY;
    }

    public int getGameTime(){
        return Main.getInstance().getGame().getTime();
    }
}
