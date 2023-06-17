package br.com.stenox.cxc.command.list;

import br.com.stenox.cxc.command.CommandBase;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.tag.Tag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TagCommand extends CommandBase {

    public TagCommand(){
        super("tag");
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());

            if (args.length == 0) {
                int max = gamer.getTags().size() * 2;
                TextComponent[] message = new TextComponent[max];
                message[0] = new TextComponent("§aSuas tags: ");
                int i = max - 1;
                for (Tag tag : gamer.getTags()) {
                    if (i < max - 1) {
                        message[i] = new TextComponent("§f, ");
                        i -= 1;
                    }
                    String portuguese = "§fExemplo: " + tag.getPrefix() + player.getName() + "\n\n§aClique para selecionar!";

                    TextComponent component = new TextComponent(tag.getColor() + tag.getName());
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(portuguese)}));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + tag.name()));
                    message[i] = component;
                    i -= 1;
                    component = null;
                }
                player.spigot().sendMessage(message);
                message = null;
            } else {
                Tag tag = Tag.fromString(args[0]);
                if (tag != null) {
                    if (gamer.getTags().contains(tag)) {
                        if (gamer.getTag() != tag) {
                            if (gamer.getTeam() == null) {
                                gamer.setTag(tag);
                                String format = tag.getPrefix() + tag.getName();

                                player.sendMessage("§aVocê selecionou a tag " + tag.getColor() + tag.getName() + " §acom sucesso.");
                            } else {
                                gamer.sendMessage("§cVocê não pode estar em um time para selecionar a tag.");
                            }
                        } else {
                            player.sendMessage("§cVocê já está utilizando a tag " + tag.getName() + ".");
                        }
                    } else {
                        player.sendMessage("§cVocê não possui permissão para usar esta tag.");
                    }
                } else {
                    player.sendMessage("§cTag não encontrada.");
                }
            }
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Gamer gamer = Gamer.getGamer(player.getUniqueId());
            if (args.length == 1) {
                String lastWord = args[args.length - 1];
                List<String> list = new ArrayList<>();
                for (Tag tags : gamer.getTags()) {
                    String name = tags.getName().toLowerCase();
                    if (StringUtil.startsWithIgnoreCase(name, lastWord))
                        list.add(name);
                }
                return list;
            }
        }
        return new ArrayList<>();
    }
}
