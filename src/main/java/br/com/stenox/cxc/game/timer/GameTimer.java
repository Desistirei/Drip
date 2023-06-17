package br.com.stenox.cxc.game.timer;

import br.com.stenox.cxc.game.Game;
import br.com.stenox.cxc.game.mode.GameMode;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.game.structures.types.BonusFeast;
import br.com.stenox.cxc.game.structures.types.Feast;
import br.com.stenox.cxc.game.structures.types.FinalBattle;
import br.com.stenox.cxc.game.structures.types.MiniFeast;
import br.com.stenox.cxc.rule.manager.RuleManager;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class GameTimer implements Runnable {

    private final Game game;

    @Getter
    @Setter
    Feast feast;

    private int rulePosition = 1;

    @Override
    public void run() {
        if (game.isRunning() && !Main.LOCK) {
            game.moveTime();

            if (game.getMode() == GameMode.CLANXCLAN) {
                if (game.getStage() == GameStage.STARTING) {

                    if (game.getTime() == 0)
                        game.setStage(GameStage.INVINCIBILITY);
                    else if ((game.getTime() % 15 == 0 || game.getTime() <= 5) && game.getTime() < 120 && game.getTime() > 0) {
                        Bukkit.broadcastMessage("§aIniciando em " + game.getFormattedLongTime());
                        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F));
                    } else if (game.getTime() == 10)
                        CompletableFuture.runAsync(() -> {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.teleport(Main.SPAWN);
                            }
                        });
                } else if (game.getStage() == GameStage.INVINCIBILITY) {
                    if (game.getTime() == 0)
                        game.setStage(GameStage.IN_GAME);
                    else {
                        if ((game.getTime() % 30 == 0 || game.getTime() <= 5) && game.getTime() > 0) {
                            Bukkit.broadcastMessage("§cInvencibilidade acabando em " + game.getFormattedLongTime());
                            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F));
                        }
                    }
                } else if (game.getStage() == GameStage.IN_GAME) {
                    Main.getInstance().getGameManager().checkWin();

                    if (game.getTime() % 300 == 0 && Variable.MINI_FAST.isActive())
                        new MiniFeast(Main.getInstance().getGameManager(), 2).spawnMiniFeast();

                    if (game.getTime() == (int) Variable.TIMINGS_FEAST.getValue() && Variable.FEAST.isActive()) {
                        if (feast == null) {
                            feast = new Feast(Main.getInstance().getGameManager(), 15);
                            feast.spawnFeast();
                        }
                    } else if (game.getTime() == (int) Variable.TIMINGS_BONUS_FEAST.getValue() && Variable.BONUS_FEAST.isActive()) {
                        BonusFeast b = new BonusFeast(Main.getInstance().getGameManager(), 15);
                        b.spawnFeast();
                    } else if (game.getTime() == (int) Variable.TIMINGS_FINAL_ARENA.getValue())
                        new FinalBattle();
                }
            } else {
                RuleManager ruleManager = Main.getInstance().getRuleManager();

                if (game.getStage() == GameStage.STARTING) {
                    if (game.getTime() <= 30 && game.getTime() % 2 == 0) {
                        if (rulePosition <= ruleManager.getRules().size()) {
                            Bukkit.broadcastMessage("§b§l[" + Main.CLAN_NAME.toUpperCase() + "] §7» §f" + ruleManager.getRule(rulePosition).getText());
                            rulePosition++;
                        }
                    }

                    if (game.getTime() == 0)
                        game.setStage(GameStage.INVINCIBILITY);
                    else if ((game.getTime() % 15 == 0 || game.getTime() <= 5) && game.getTime() < 120 && game.getTime() != 30 && game.getTime() > 0) {
                        Bukkit.broadcastMessage("§aIniciando em " + game.getFormattedLongTime());
                        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F));
                    } else if (game.getTime() == 10) {
                        CompletableFuture.runAsync(() -> {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.teleport(Main.SPAWN);
                            }
                        });
                    }
                } else if (game.getStage() == GameStage.INVINCIBILITY) {
                    if (game.getTime() == 0) {
                        game.setStage(GameStage.IN_GAME);
                    } else {
                        if ((game.getTime() % 30 == 0 || game.getTime() <= 5) && game.getTime() > 0) {
                            Bukkit.broadcastMessage("§cInvencibilidade acabando em " + game.getFormattedLongTime());
                            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F));
                        }
                    }
                } else if (game.getStage() == GameStage.IN_GAME) {
                    Main.getInstance().getGameManager().checkWin();

                    if (game.getTime() % 300 == 0 && Variable.MINI_FAST.isActive())
                        new MiniFeast(Main.getInstance().getGameManager(), 2).spawnMiniFeast();
                    if (game.getTime() == (int) Variable.TIMINGS_FEAST.getValue() && Variable.FEAST.isActive()) {
                        if (feast == null) {
                            feast = new Feast(Main.getInstance().getGameManager(), 15);
                            feast.spawnFeast();
                        }
                    }
                    if (game.getTime() == 1560 && Variable.BONUS_FEAST.isActive()) {
                        BonusFeast b = new BonusFeast(Main.getInstance().getGameManager(), 15);
                        b.spawnFeast();
                    }

                    if (game.getTime() == (int) Variable.TIMINGS_FINAL_ARENA.getValue() && Variable.FINAL_BATTLE.isActive())
                        new FinalBattle();
                }
            }
        }
    }

    public void teleport() {
        double x = Main.SPAWN.getX();
        double z = Main.SPAWN.getZ();
        double y = Main.SPAWN.getY();
        Location loc = new Location(Main.SPAWN.getWorld(), x, y, z);
        Iterator<? extends Player> playerIterator = Bukkit.getOnlinePlayers().iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerIterator.hasNext()) {
                    for (int i = 0; i < 7; i++) {
                        if (!playerIterator.hasNext()) {
                            cancel();
                            return;
                        }
                        Player player = playerIterator.next();
                        player.teleport(loc);
                    }
                } else {
                    cancel();
                }

            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 10L);
    }
}
