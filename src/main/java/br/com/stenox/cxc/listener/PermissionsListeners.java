package br.com.stenox.cxc.listener;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.group.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Arrays;
import java.util.List;

public class PermissionsListeners implements Listener {

    private final static List<String> PERMISSIONS = Arrays.asList("minecraft.command.worldborder", "minecraft.command.whitelist",
            "minecraft.command.kill", "minecraft.command.toggledownfall", "minecraft.command.give", "minecraft.command.clear", "minecraft.command.give",
            "minecraft.command.ban-ip", "minecraft.command.banlist", "minecraft.command.effect", "minecraft.command.enchant", "minecraft.command.xp",
            "worldedit.history.undo", "worldedit.history.undo.self", "worldedit.selection.pos", "worldedit.wand", "worldedit.region.set", "worldedit.region.walls",
            "worldedit.schematic.delete", "worldedit.schematic.list", "worldedit.clipboard.load", "worldedit.schematic.save", "worldedit.schematic.formats", "worldedit.schematic.load",
            "worldedit.clipboard.save", "worldedit.clipboard.paste", "worldedit.clipboard.copy", "worldedit.tool.replacer", "worldedit.navigation.up",
            "worldedit.selection.hpos", "worldedit.generation.cylinder", "worldedit.generation.sphere", "worldedit.clipboard.rotate", "worldedit.clipboard.flip",
            "worldedit.fill");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Gamer gamer = Gamer.getGamer(player.getUniqueId());

        if (gamer.getGroup().ordinal() <= Group.MOD.ordinal()) {
            PermissionAttachment attachment = player.addAttachment(Main.getInstance());
            for (String permission : PERMISSIONS) {
                attachment.setPermission(permission, true);
            }
        }
    }
}
