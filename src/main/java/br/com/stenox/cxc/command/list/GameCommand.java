package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.Formatter;
import br.com.stenox.cxc.utils.StringUtils;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameCommand extends CommandBase {

    public GameCommand() {
        super("game");
    }

    private final List<String> modes = Arrays.asList("Solo", "Dupla", "Trio", "Panela", "ClanXClan");

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!hasPermission(sender, Group.MOD)) {
            return false;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("title")) {
                if (args.length >= 2) {
                    String title = StringUtils.createArgs(1, args, "", true);

                    if (ChatColor.stripColor(title).length() > 16){
                        sender.sendMessage("§cTamanho máximo do título é de 16 letras.");
                        return false;
                    }

                    Main.SCOREBOARD_TITLE = title;
                    sender.sendMessage("§aTítulo alterado com sucesso.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("respawn")) {
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage("§cJogador não encontrado.");
                        return false;
                    }
                    Gamer gamer = Gamer.getGamer(target.getUniqueId());
                    if (gamer.isAlive()) {
                        sender.sendMessage("§cEsse jogador já está vivo.");
                        return false;
                    } else if (gamer.isVanish()) {
                        sender.sendMessage("§cEsse jogador está no vanish.");
                        return false;
                    }
                    gamer.setAlive(true);

                    target.setFlying(false);
                    target.setAllowFlight(false);
                    target.teleport(Main.SPAWN);
                    target.setGameMode(GameMode.SURVIVAL);

                    if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        target.removePotionEffect(PotionEffectType.INVISIBILITY);

                    Main.getInstance().getGameManager().giveItems(gamer);

                    Main.getInstance().getGamerProvider().updatePlayers();

                    sender.sendMessage("§aJogador revivido.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("time")){
                if (args.length == 2) {
                    if (!isInteger(args[1])) {
                        sender.sendMessage("§cNumero inválido.");
                        return false;
                    }
                    int time = Integer.parseInt(args[1]);

                    Main.getInstance().getGame().setTime(time);

                    if (Main.getInstance().getGame().getStage() == GameStage.INVINCIBILITY) {
                        if (time != 1)
                            Main.LAST_INVINCIBILITY_TIME = time;
                    }

                    Bukkit.broadcastMessage("§aTempo alterado para " + Formatter.toTimeLong(time));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("defaultkit")){
                if (args.length == 2){
                    Kit kit = null;
                    for (Kit kits : Main.getInstance().getKitManager().getKits()) {
                        if (kits.getName().equalsIgnoreCase(args[1])){
                            kit = kits;
                            break;
                        }
                    }
                    if (kit == null && !args[1].equalsIgnoreCase("nenhum")){
                        sender.sendMessage("§cKit não encontrado.");
                        return false;
                    }
                    Main.DEFAULT_KIT = kit;

                    for (Gamer gamer : Main.getInstance().getGamerProvider().getGamers()) {
                        if (gamer.getPlayer() == null) continue;
                        gamer.setKit(kit);

                        gamer.sendMessage("§aVocê recebeu o kit " + gamer.getKitName() + ".");
                    }

                    sender.sendMessage("§aKit padrão definido como: " + kit.getName());
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("starttimer")){
                if (Main.getInstance().getGame().isRunning()){
                    sender.sendMessage("§cO timer já está contando.");
                    return false;
                }
                Main.getInstance().getGame().setRunning(true);
                Bukkit.broadcastMessage("§aO timer foi iniciado.");
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn")){
                if (args.length == 1){
                    if (!isPlayer(sender)){
                        sender.sendMessage("§cEste comando é apenas para jogadores.");
                        return false;
                    }
                    Player player = (Player) sender;

                    Main.SPAWN = player.getLocation();

                    player.sendMessage("§aSpawn setado com sucesso.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("prefs")){
                sender.sendMessage("");
                sender.sendMessage("§fBuild: " + (Variable.BUILD.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fDano: " + (Variable.DAMAGE.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fCombate: " + (Variable.COMBAT.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fDrops: " + (Variable.DROPS.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fUso de baldes: " + (Variable.ALLOW_BUCKETS.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fBonusFeast: " + (Variable.BONUS_FEAST.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fKits Free: " + (Variable.ALL_KITS_FREE.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fRespawn: " + (Variable.RESPAWN.isActive() ? "§a§lATIVO" : "§c§lDESATIVADO"));
                sender.sendMessage("§fLimite de jogadores: §b" + Bukkit.getMaxPlayers());
                sender.sendMessage("");
                return true;
            } else if (args[0].equalsIgnoreCase("mode")){
                if (args.length == 2){
                    Main.GAME_MODE = args[1];

                    sender.sendMessage("§aModo de jogo alterado.");
                    return true;
                }
            }
        }
        sender.sendMessage("§cUsage:");
        sender.sendMessage("§c/game title [title]");
        sender.sendMessage("§c/game respawn [player]");
        sender.sendMessage("§c/game time [time]");
        sender.sendMessage("§c/game defaultkit [kit]");
        sender.sendMessage("§c/game starttimer");
        sender.sendMessage("§c/game setspawn");
        sender.sendMessage("§c/game prefs");
        sender.sendMessage("§c/game mode");
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> subCommands = Arrays.asList("title", "respawn", "time", "defaultkit", "starttimer", "setspawn", "prefs", "mode");

        if (args.length == 1){
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();
            for (String s : subCommands) {
                if (StringUtil.startsWithIgnoreCase(s, lastWord))
                    list.add(s);
            }
            return list;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("respawn")){
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();
            for (Gamer gamer : Main.getInstance().getGameManager().getDeadPlayers()) {
                if (gamer.isVanish() || gamer.getPlayer() == null) continue;
                String name = gamer.getFake() != null ? gamer.getFake() : gamer.getName();
                if (StringUtil.startsWithIgnoreCase(name, lastWord))
                    list.add(name);
            }
            return list;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("defaultkit")){
            String lastWord = args[args.length - 1];
            List<String> list = new ArrayList<>();
            for (Kit kit : Main.getInstance().getKitManager().getKits()) {
                String name = kit.getName().toLowerCase();
                if (StringUtil.startsWithIgnoreCase(name, lastWord))
                    list.add(name);
            }
            return list;
        }

        return Collections.emptyList();
    }
}
