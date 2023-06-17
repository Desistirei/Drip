package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.tag.Tag;
import br.com.stenox.cxc.utils.SkinLibrary;
import br.com.stenox.cxc.utils.mojang.PlayerAPI;
import br.com.stenox.cxc.utils.mojang.disguise.SkinProvider;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NickCommand extends CommandBase {

    public List<String> nickNames = new ArrayList<>();

    public NickCommand() {
        super("nick");
    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (sender instanceof Player) {
            if (!hasPermission(sender, Group.ELITE))
                return false;

            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (args.length == 0) {
                player.sendMessage("§cUso de /" + lb + ":");
                player.sendMessage("§c- /" + lb + " <nickname>");
                player.sendMessage("§c- /" + lb + " reset");
                player.sendMessage("§c- /" + lb + " skin <skin>");
            } else {
                String fakeName = args[0];
                if (fakeName.equalsIgnoreCase("random")) {
                    String x = Main.getNameFetcher().random();
                    if (x == null || x.equals("null"))
                        x = Main.getNameFetcher().random();
                    String random = x + randomWord();
                    player.performCommand("nick " + random);
                    return false;
                } else if (fakeName.equalsIgnoreCase("list")) {
                    if (hasPermission(sender, Group.YOUTUBER_PLUS)) {
                        List<Gamer> withFake = new ArrayList<>();
                        for (Gamer w : Main.getInstance().getGamerProvider().getGamers()) {
                            if (w.getFake() == null)
                                continue;
                            withFake.add(w);
                        }
                        if (withFake.size() > 0) {
                            for (Gamer inFake : withFake) {
                                if (gamer.getGroup().ordinal() <= inFake.getGroup().ordinal())
                                    player.sendMessage("§8[§7" + inFake.getName() + " §8§l> §e" + inFake.getFake() + "§8]");
                            }
                        } else {
                            player.sendMessage("§cNão há nenhum jogador utilizando um nickname nesta sala.");
                        }
                        withFake = null;
                    } else {
                        player.sendMessage("§cVocê não possui permissão para ver a lista de nicknames.");
                    }
                    return false;
                } else if (fakeName.equalsIgnoreCase("reset")) {
                    player.performCommand("reset nick");
                    return false;
                } else if (fakeName.equalsIgnoreCase("skin")) {
                    if (args.length == 2) {
                        String skin = args[1];

                        if (skin == null) {
                            player.sendMessage("§cUso correto: /nick skin <skin>");
                            return false;
                        }

                        if (!PlayerAPI.validateName(skin, player.getUniqueId())) {
                            player.sendMessage("§cEste nickname é inválido.");
                            return false;
                        }

                        String[] properties = PlayerAPI.getFromName(skin);

                        if (properties == null) {
                            sender.sendMessage("§cSkin não encontrada.");
                            return false;
                        }

                        Property property = new Property("textures", properties[0], properties[1]);

                        new BukkitRunnable() {
                            public void run() {
                                SkinProvider.setSkin(player, property);
                                player.sendMessage("§aVocê alterou a sua skin para " + skin + ".");
                            }
                        }.runTask(Main.getInstance());

                        gamer.setSkinValue(properties[0]);
                        gamer.setSkinSignature(properties[1]);
                        return true;
                    }
                    player.sendMessage("§cUso de /" + lb + ":");
                    player.sendMessage("§c- /" + lb + " <nickname>");
                    player.sendMessage("§c- /" + lb + " reset");
                    player.sendMessage("§c- /" + lb + " skin <skin>");
                } else {
                    if (gamer.getGroup().ordinal() <= Group.ADMIN.ordinal() || Main.getInstance().getGamerProvider().search(fakeName) == null) {
                        if (gamer.getGroup().ordinal() <= Group.ADMIN.ordinal() || PlayerAPI.validateName(fakeName, player.getUniqueId())) {
                            if (Bukkit.getPlayerExact(fakeName) == null) {
                                if (nickNames.contains(fakeName.toLowerCase())) {
                                    sender.sendMessage("§cBump! este nickname já foi solicitado por outro jogador anteriormente.");
                                    return false;
                                }
                                nickNames.add(fakeName.toLowerCase());
                                UUID uuid = Main.getUUIDOf(fakeName);
                                if (uuid != null && gamer.getGroup().ordinal() > Group.ADMIN.ordinal()) {
                                    sender.sendMessage(ChatColor.RED + "Alguém já está usando este nickname. (Mojang)");
                                    nickNames.remove(fakeName.toLowerCase());
                                    return false;
                                }
                                if (Main.getInstance().getGamerRepository().contains(Main.getOfflineUUID(fakeName))) {
                                    sender.sendMessage(ChatColor.RED + "Alguém já está usando este nickname. (Database)");
                                    nickNames.remove(fakeName.toLowerCase());
                                    return false;
                                }
                                if (fakeName.length() > 16) {
                                    sender.sendMessage("§cNome muito grande.");
                                    nickNames.remove(fakeName.toLowerCase());
                                    return false;
                                }
                                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                    gamer.setFake(fakeName);

                                    PlayerAPI.changePlayerName(player, fakeName);

                                    Main.getInstance().getTagProvider().setTag(player, Tag.MEMBER);

                                    player.sendMessage("§aVocê alterou o seu nickname para " + fakeName + ".");

                                    if (!gamer.isVanish())
                                        show(player);

                                    if (Bukkit.getOnlinePlayers().size() > 1) {
                                        ArrayList<Player> players = new ArrayList<Player>();
                                        Bukkit.getOnlinePlayers().forEach(online -> {
                                            if (online != player)
                                                players.add(online);
                                        });
                                        Player sorteado = players.get(new Random().nextInt(players.size()));

                                        Gamer target = Gamer.getGamer(sorteado.getUniqueId());

                                        while (target == null || target.getFake() != null){
                                            sorteado = players.get(new Random().nextInt(players.size()));

                                            target = Gamer.getGamer(sorteado.getUniqueId());
                                        }

                                        String[] properties = PlayerAPI.getFromPlayer(sorteado);
                                        Property property = new Property("textures", properties[0], properties[1]);

                                        new BukkitRunnable() {
                                            public void run() {
                                                SkinProvider.setSkin(player, property);
                                            }
                                        }.runTask(Main.getInstance());

                                        gamer.setSkinValue(properties[0]);
                                        gamer.setSkinSignature(properties[1]);
                                    } else {
                                        SkinLibrary library = SkinLibrary.getRandomSkin();
                                        Property property = new Property("textures", library.getValue(), library.getSignature());

                                        new BukkitRunnable() {
                                            public void run() {
                                                SkinProvider.setSkin(player, property);
                                            }
                                        }.runTask(Main.getInstance());

                                        gamer.setSkinValue(library.getValue());
                                        gamer.setSkinSignature(library.getSignature());
                                    }
                                    gamer.setFake(fakeName);
                                    gamer.setTag(Tag.MEMBER);
                                    nickNames.remove(fakeName.toLowerCase());
                                });

                            } else {
                                player.sendMessage("§cAlguém já está usando este nickname.");
                                nickNames.remove(fakeName.toLowerCase());
                            }
                        } else {
                            player.sendMessage("§cEste nickname é inválido.");
                            nickNames.remove(fakeName.toLowerCase());
                        }
                    } else {
                        player.sendMessage("§cAlguém já está usando este nickname. (Mojang)");
                        nickNames.remove(fakeName.toLowerCase());
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void show(Player faker) {
        for (Player geral : Bukkit.getServer().getOnlinePlayers()) {
            geral.hidePlayer(faker);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> geral.showPlayer(faker), 5L);
        }
    }

    public enum DISPLAYS {

        RESET("reset"), SKIN("skin"), RANDOM("random");

        public String name;

        DISPLAYS(String name) {
            this.name = name;
        }

        public String getName() {
            return name;

        }
    }

    public boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String randomWord() {
        int i = new Random().nextInt(8);
        if (i == 0) {
            return "_";
        } else if (i == 1) {
            return "br";
        } else if (i == 2) {
            return "pvp";
        } else if (i == 3) {
            return "PvP";
        } else if (i == 4) {
            return "BR";
        } else if (i == 5) {
            return "BDF";
        } else if (i == 6) {
            return "zZ";
        } else if (i == 7) {
            return "brZin";
        }
        return "_";
    }
}
