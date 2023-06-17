package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.utils.Manager;
import br.com.stenox.cxc.utils.Schematic;
import org.bukkit.Bukkit;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class ArenaCommand extends CommandBase {

    private final ArrayList<Block> smallArena;
    private final ArrayList<Block> normalArena;
    private final ArrayList<Block> bigArena;
    private final ArrayList<Block> extremeArena;

    public ArenaCommand() {
        super("arena");
        this.smallArena = new ArrayList<>();
        this.normalArena = new ArrayList<>();
        this.bigArena = new ArrayList<>();
        this.extremeArena = new ArrayList<>();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (hasPermission(sender, Group.MOD)) {
            if (args.length == 0) {
                player.sendMessage("§cUtilize: /arena (pequena|media|grande|extrema)");
                return false;
            }
            if (args[0].equalsIgnoreCase("pequena")) {
                try {
                    Schematic schematic = Schematic.getInstance().carregarSchematics(new File(Main.getInstance().getDataFolder(), "arenapequena.schematic"));
                    Schematic.getInstance().generateSchematic(Bukkit.getWorlds().get(0), player.getLocation(), schematic, this.smallArena);
                    player.sendMessage("§aVoc\u00ea spawnou a arena pequena!");
                    Manager.getInstance().sendWarn(player, "O " + player.getName() + " criou a arena pequena!");
                } catch (Exception e) {
                    player.sendMessage("§cOcorreu um erro ao tentar spawnar a arena pequena!");
                }
            } else if (args[0].equalsIgnoreCase("media")) {
                try {
                    Schematic schematic = Schematic.getInstance().carregarSchematics(new File(Main.getInstance().getDataFolder(), "arenamedia.schematic"));
                    Schematic.getInstance().generateSchematic(Bukkit.getWorlds().get(0), player.getLocation(), schematic, this.normalArena);
                    player.sendMessage("§aVoc\u00ea spawnou a arena m\u00e9dia!");
                    Manager.getInstance().sendWarn(player, "O " + player.getName() + " criou a arena m\u00e9dia!");
                } catch (Exception e) {
                    player.sendMessage("§cOcorreu um erro ao tentar spawnar a arena pequena!");
                }
            } else if (args[0].equalsIgnoreCase("grande")) {
                try {
                    Schematic schematic = Schematic.getInstance().carregarSchematics(new File(Main.getInstance().getDataFolder(), "arenagrande.schematic"));
                    Schematic.getInstance().generateSchematic(Bukkit.getWorlds().get(0), player.getLocation(), schematic, this.bigArena);
                    player.sendMessage("§aVoc\u00ea spawnou a arena grande!");
                    Manager.getInstance().sendWarn(player, "O " + player.getName() + " criou a arena grande!");
                } catch (Exception e) {
                    player.sendMessage("§cOcorreu um erro ao tentar spawnar a arena pequena!");
                }
            } else if (args[0].equalsIgnoreCase("extrema")) {
                try {
                    Schematic schematic = Schematic.getInstance().carregarSchematics(new File(Main.getInstance().getDataFolder(), "arenaextrema.schematic"));
                    Schematic.getInstance().generateSchematic(Bukkit.getWorlds().get(0), player.getLocation(), schematic, this.extremeArena);
                    player.sendMessage("§aVoc\u00ea spawnou a arena extrema!");
                    Manager.getInstance().sendWarn(player, "O " + player.getName() + " criou a arena extrema!");
                } catch (Exception e) {
                    player.sendMessage("§cOcorreu um erro ao tentar spawnar a arena pequena!");
                }
            }
        }
        return false;
    }
}