package br.com.stenox.cxc;

import br.com.stenox.cxc.command.list.*;
import br.com.stenox.cxc.command.list.punishments.*;
import br.com.stenox.cxc.database.SQLite;
import br.com.stenox.cxc.event.custom.TimeSecondEvent;
import br.com.stenox.cxc.game.Game;
import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.game.mode.GameMode;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.GamerRepository;
import br.com.stenox.cxc.gamer.provider.GamerProvider;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.kit.manager.KitManager;
import br.com.stenox.cxc.listener.*;
import br.com.stenox.cxc.login.protocol.LoginReceiver;
import br.com.stenox.cxc.login.util.Storage;
import br.com.stenox.cxc.login.util.Util;
import br.com.stenox.cxc.login.version.Version;
import br.com.stenox.cxc.rule.manager.RuleManager;
import br.com.stenox.cxc.tag.TagProvider;
import br.com.stenox.cxc.utils.ConfigUtils;
import br.com.stenox.cxc.utils.restapi.UUIDFetcher;
import br.com.stenox.cxc.utils.restapi.name.NameFetcher;
import br.com.stenox.cxc.utils.scoreboard.sidebar.Sidebar;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    private TagProvider tagProvider;

    private GamerProvider gamerProvider;

    private Game game;
    private GamerRepository gamerRepository;

    private ConfigUtils configuration;

    private File miniFeastFile;

    private SQLite connection;

    private ProtocolManager protocolManager;

    private Storage storage;

    private FileConfiguration rulesConfig;

    private GameManager gameManager;
    private KitManager kitManager;
    private RuleManager ruleManager;

    /*------------------------------------------
    * Static
    * ------------------------------------------*/

    public static boolean LOCK;
    public static boolean CHAT = true;
    public static boolean TOGGLE_KITS = true;

    public static Kit DEFAULT_KIT;

    public static Location SPAWN;

    public static String SCOREBOARD_TITLE, CLAN_NAME;

    @Getter
    private final static Gson gson = new Gson();

    @Getter
    private static NameFetcher nameFetcher;
    private static UUIDFetcher uuidFetcher;

    public static String GAME_MODE;

    public static int LAST_INVINCIBILITY_TIME = 60;

    @Override
    public void onLoad() {
        getLogger().info("Plugin loading...");

        getServer().unloadWorld("world_nether", false);
        deleteDir(new File("world_nether"));

        getServer().unloadWorld("world", false);
        deleteDir(new File("world"));

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
            world.setAutoSave(false);
            world.setTime(0L);

            if (world.getName().equalsIgnoreCase("world"))
                continue;

            world.setGameRuleValue("doMobSpawning", "false");
        }

        protocolManager = ProtocolLibrary.getProtocolManager();

        getLogger().info("Plugin loaded");
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Plugin initialising...");

        saveDefaultConfig();

        File rulesFile = new File(getDataFolder(), "rules.yml");
        if (!rulesFile.exists())
            saveResource("rules.yml", true);

        rulesConfig = YamlConfiguration.loadConfiguration(rulesFile);

        ruleManager = new RuleManager(this);

        connection = new SQLite(this);
        connection.openConnection();
        connection.createTables();

        configuration = new ConfigUtils(this);

        SPAWN = new Location(Bukkit.getWorld("world"), 0, Bukkit.getWorld("world").getHighestBlockYAt(0, 0), 0);

        miniFeastFile = new File(getDataFolder(), "mini-feast.bo3");
        if (!miniFeastFile.exists())
            saveResource("mini-feast.bo3", true);

        tagProvider = new TagProvider(this);
        tagProvider.onEnable();

        gamerProvider = new GamerProvider(this);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(connection.getUrl());

        gamerRepository = new GamerRepository(dataSource);

        game = new Game(br.com.stenox.cxc.game.mode.GameMode.valueOf(configuration.getString("mode")));
        gameManager = new GameManager(gamerProvider);

        kitManager = new KitManager(gameManager);
        kitManager.loadKits();

         storage = new Storage();

        CLAN_NAME = getConfiguration().getString("names.clanName");

        SCOREBOARD_TITLE = game.getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN ? "§b§lCLAN X CLAN" : "§b§l" + CLAN_NAME.toUpperCase();

        LAST_INVINCIBILITY_TIME = game.getMode() == GameMode.CLANXCLAN ? GameStage.INVINCIBILITY.getStartTimeCxC() : GameStage.INVINCIBILITY.getStartTimeEvent();

        nameFetcher = new NameFetcher(dataSource);
        uuidFetcher = new UUIDFetcher(dataSource);

        if (!this.getServer().getOnlineMode())
            Util.setOnlineMode(true);

        if (Version.getPackageVersion() == null)
            this.getServer().getPluginManager().disablePlugin(this);

        new LoginReceiver();

        Bukkit.getPluginManager().registerEvents(new AuthListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CommandListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);

        EnchantmentTableAutoLapisListener.init();

        Bukkit.getPluginManager().registerEvents(new EnchantmentTableAutoLapisListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListeners(), this);
        Bukkit.getPluginManager().registerEvents(new LogsListeners(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListeners(), this);
        Bukkit.getPluginManager().registerEvents(new MinerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new MotdListener(), this);
        Bukkit.getPluginManager().registerEvents(new PermissionsListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorsListeners(), this);
        Bukkit.getPluginManager().registerEvents(new StartingListeners(), this);

        if (game.getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN)
            Bukkit.getPluginManager().registerEvents(new TeamListener(), this);
        else
            Bukkit.getPluginManager().registerEvents(new GamerSearchListener(), this);

        Bukkit.getPluginManager().registerEvents(new VanishListeners(), this);
        Bukkit.getPluginManager().registerEvents(new VariableLockListeners(), this);

        unregisterCommands("me", "pl", "plugins", "icanhasbukkit", "ver", "version", "?", "help", "viaversion", "viaver", "vvbukkit", "protocolsupport", "ps", "holograms", "hd", "holo", "hologram", "ban", "me", "say", "about", "pardon", "pardon-ip", "ban-ip", "ipwhitelist", "trigger", "testforblock", "tellraw", "testfor", "testforblocks", "playsounds", "title", "summon", "fill", "entitydata", "particle", "replaceitem", "execute", "clone", "debug", "packet_filter", "achievement", "kick", "help", "tell", "ban", "ban-ip", "banlist", "message", "msg", "xp", "stats", "kill", "spawnpoint", "spreadplayers", "filter", "list", "playsound", "rl", "reload", "seed", "setidletimeout", "scoreboard", "tp", "teleport", "gamemode", "packet", "protocol", "packetlog", "blockdata", "defaultgamemode", "filter", "restart");

        CommandMap map = ((CraftServer) getServer()).getCommandMap();

        map.register("ban", new BanCommand());
        map.register("mute", new MuteCommand());
        map.register("tempban", new TempBanCommand());
        map.register("tempmute", new TempMuteCommand());
        map.register("unban", new UnbanCommand());
        map.register("unmute", new UnmuteCommand());
        map.register("kick", new KickCommand());

        map.register("account", new AccountCommand());
        map.register("arena", new ArenaCommand());
        map.register("broadcast", new BroadcastCommand());
        map.register("chat", new ChatCommand());
        map.register("cleardrops", new ClearDropsCommand());
        map.register("compass", new CompassCommand());
        map.register("crash", new CrashCommand());
        map.register("createarena", new CreateArenaCommand());
        map.register("feast", new FeastCommand());
        map.register("forcefeast", new ForceFeastCommand());
        map.register("forcekit", new ForceKitCommand());
        map.register("game", new GameCommand());
        map.register("gamemode", new GamemodeCommand());
        map.register("groupset", new GroupSetCommand());
        map.register("invsee", new InvseeCommand());
        map.register("lock", new LockCommand());
        map.register("login", new LoginCommand());
        map.register("nick", new NickCommand());
        map.register("ping", new PingCommand());
        map.register("playerfinder", new PlayerFinderCommand());
        map.register("register", new RegisterCommand());
        map.register("reply", new ReplyCommand());
        map.register("report", new ReportCommand());
        map.register("reset", new ResetCommand(this));
        map.register("rules", new RulesCommand());
        map.register("spawn", new SpawnCommand());
        map.register("skit", new SpecialKitCommand());
        map.register("spectators", new SpectatorsCommand());
        map.register("staffchat", new StaffChatCommand());
        map.register("tag", new TagCommand());
        map.register("tpall", new TeleportAllCommand());
        map.register("teleport", new TeleportCommand());
        map.register("tell", new TellCommand());
        map.register("togglekit", new ToggleKitCommand());
        map.register("vanish", new VanishCommand());
        map.register("variable", new VariableCommand());

        for (World worlds : Bukkit.getWorlds()) {
            worlds.setDifficulty(Difficulty.EASY);
            worlds.setGameRuleValue("naturalRegeneration", "false");
            worlds.setGameRuleValue("randomTickSpeed", "0");
            worlds.setAnimalSpawnLimit(10);
            worlds.setAutoSave(false);
            worlds.setStorm(false);
            worlds.setGameRuleValue("doMobSpawning", "false");
            worlds.setThundering(false);
            worlds.setWeatherDuration(Integer.MIN_VALUE);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                new TimeSecondEvent(TimeSecondEvent.TimeType.SECONDS).call();
            }
        }.runTaskTimer(this, 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                new TimeSecondEvent(TimeSecondEvent.TimeType.MILLISECONDS).call();
            }
        }.runTaskTimer(this, 0L, 1L);

        updateScoreboard();

        generateRecipes();

        getLogger().info("Plugin initialized");
    }

    @Override
    public void onDisable() {
        tagProvider.onDisable();
        tagProvider = null;

        for (Gamer gamer : getGamerProvider().getGamers())
            gamerProvider.update(gamer);

        ruleManager.saveRules();

        connection.closeConnection();
    }

    public static UUID getUUIDOf(final String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        } else {
            return uuidFetcher.getUUID(name);
        }
    }

    public static UUID getOfflineUUID(String username) {
        String data = "OfflinePlayer:" + username.toUpperCase();
        return UUID.nameUUIDFromBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    public void generateRecipes() {
        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);

        ShapelessRecipe cocoa = new ShapelessRecipe(soup);
        ShapelessRecipe cactus = new ShapelessRecipe(soup);
        ShapelessRecipe pumpkin = new ShapelessRecipe(soup);
        ShapelessRecipe melon = new ShapelessRecipe(soup);

        cocoa.addIngredient(Material.BOWL);
        cocoa.addIngredient(Material.INK_SACK, 3);

        cactus.addIngredient(Material.BOWL);
        cactus.addIngredient(Material.CACTUS);

        pumpkin.addIngredient(Material.BOWL);
        pumpkin.addIngredient(Material.PUMPKIN_SEEDS);

        melon.addIngredient(Material.BOWL);
        melon.addIngredient(Material.MELON_SEEDS);

        Bukkit.addRecipe(cocoa);
        Bukkit.addRecipe(cactus);
        Bukkit.addRecipe(pumpkin);
        Bukkit.addRecipe(melon);
    }

    private void deleteDir(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(file, children[i]));
            }
        }
        file.delete();
    }

    private void updateScoreboard() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Gamer gamer = Gamer.getGamer(player.getUniqueId());
                    if (gamer == null || gamer.getWrapper().getSidebar().isHided())
                        continue;

                    Sidebar sidebar = gamer.getWrapper().getSidebar();

                    sidebar.setTitle(SCOREBOARD_TITLE);

                    if (game.getStage() == GameStage.STARTING) {
                        if (game.getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fIniciando em: §7" + game.getFormattedTime());
                                rows.add("§fTimes:");
                                Arrays.stream(GamerTeam.values()).filter(GamerTeam::isEnabled).forEach(team -> {
                                    rows.add(" " + team.getColor() + (gamer.getTeam() == team ? "§o" : "") + team.getName() + ": §f" + gamerProvider.getPlayersContForTeam(team) + " vivos");
                                });
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        } else {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fIniciando em: §7" + game.getFormattedTime());
                                rows.add("§fJogadores: §7" + gamerProvider.getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        }
                    } else if (game.getStage() == GameStage.INVINCIBILITY) {
                        if (game.getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fInvencibilidade: §7" + game.getFormattedTime());
                                rows.add("§fTimes:");
                                Arrays.stream(GamerTeam.values()).filter(GamerTeam::isEnabled).forEach(team -> {
                                    rows.add(" " + team.getColor() + (gamer.getTeam() == team ? "§o" : "") + team.getName() + ": §f" + gamerProvider.getPlayersContForTeam(team) + " vivos");
                                });
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        } else {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fInvencibilidade: §7" + game.getFormattedTime());
                                rows.add("§fJogadores: §7" + gamerProvider.getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        }
                    } else if (game.getStage() == GameStage.IN_GAME) {
                        if (game.getMode() == br.com.stenox.cxc.game.mode.GameMode.CLANXCLAN) {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fTempo: §7" + game.getFormattedTime());
                                rows.add("§fTimes:");
                                Arrays.stream(GamerTeam.values()).filter(GamerTeam::isEnabled).forEach(team -> {
                                    rows.add(" " + team.getColor() + (gamer.getTeam() == team ? "§o" : "") + team.getName() + ": §f" + gamerProvider.getPlayersContForTeam(team) + " vivos");
                                });
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                } else {
                                    rows.add("§fKills: §a" + gamer.getKills());
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        } else {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fTempo: §7" + game.getFormattedTime());
                                rows.add("§fJogadores: §7" + gamerProvider.getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                } else {
                                    rows.add("§fKills: §a" + gamer.getKills());
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        }
                    } else if (game.getStage() == GameStage.ENDING) {
                        if (game.getMode() == GameMode.CLANXCLAN) {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fTimes:");
                                Arrays.stream(GamerTeam.values()).filter(GamerTeam::isEnabled).forEach(team -> {
                                    rows.add(" " + team.getColor() + (gamer.getTeam() == team ? "§o" : "") + team.getName() + ": §f" + gamerProvider.getPlayersContForTeam(team) + " vivos");
                                });
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                } else {
                                    rows.add("§fKills: §a" + gamer.getKills());
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        } else {
                            sidebar.updateRows(rows -> {
                                rows.add("  ");
                                rows.add("§fTempo: §7" + game.getFormattedTime());
                                rows.add("§fJogadores: §7" + gamerProvider.getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
                                if (gamer.getKit() != null) {
                                    rows.add("  ");
                                    rows.add("§fKit: §a" + gamer.getKitName());
                                }
                                if (gamer.isVanish()){
                                    rows.add("  ");
                                    rows.add("§cMODO VANISH");
                                } else {
                                    rows.add("§fKills: §a" + gamer.getKills());
                                }
                                rows.add("  ");
                                rows.add("§fModo: §a" + GAME_MODE);
                                rows.add("  ");
                                rows.add("§e" + getConfiguration().getString("names.serverIp"));
                            });
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 3L);
    }

    @SuppressWarnings("unchecked")
    public static void unregisterCommands(String... commands) {
        try {
            Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f1.setAccessible(true);
            CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
            Field f2 = commandMap.getClass().getDeclaredField("knownCommands");
            f2.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);
            for (String command : commands) {
                if (knownCommands.containsKey(command)) {
                    knownCommands.remove(command);
                    List<String> aliases = new ArrayList<>();
                    for (String key : knownCommands.keySet()) {
                        if (!key.contains(":"))
                            continue;
                        String substr = key.substring(key.indexOf(":") + 1);
                        if (substr.equalsIgnoreCase(command)) {
                            aliases.add(key);
                        }
                    }
                    for (String alias : aliases) {
                        knownCommands.remove(alias);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}