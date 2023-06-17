package br.com.stenox.cxc.gamer;

import br.com.stenox.cxc.gamer.logs.LogType;
import br.com.stenox.cxc.gamer.state.GamerState;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import br.com.stenox.cxc.utils.TitleAPI;
import br.com.stenox.cxc.utils.scoreboard.ScoreboardWrapper;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.event.player.PlayerChangeStateEvent;
import br.com.stenox.cxc.gamer.group.Group;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.tag.Tag;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Gamer {

    private Player player;

    @Setter
    private String name, fake, muteReason, banReason, ip, skinValue, skinSignature, password;

    private final UUID uniqueId;

    private GamerTeam team;

    private Tag tag;

    @Setter
    private boolean alive, disconnect, vanish, invisible, tell, muted, banned, spectators, staffChat, staffMessages, online;

    @Setter
    private long groupTime, muteTime, banTime;

    @Setter
    private ScoreboardWrapper wrapper;

    @Setter
    private int kills, fighting;

    @Setter
    private Gamer lastCombat;

    @Setter
    private ItemStack[] contents;

    @Setter
    private ItemStack[] armorContents;

    private GamerState state;

    private Group group;

    @Setter
    private Kit kit;

    @Setter
    private UUID lastTell;

    public Gamer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        alive = true;
        this.state = GamerState.NORMAL;
        this.group = Group.MEMBER;
        this.groupTime = -1;
        this.tell = true;
    }

    public void sendMessage(String message) {
        if (getPlayer() == null) return;
        getPlayer().sendMessage(message);
    }

    public void setTag(Tag tag) {
        this.tag = tag;

        Main.getInstance().getTagProvider().setTag(getPlayer(), tag);
    }

    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();

        for (Tag tag : Tag.values()) {
            if (tag.getTeam().equals("X")) continue;

            if (group == Group.ELITE && tag == Tag.ELITE) continue;
            if ((group == Group.STREAMER || group == Group.YOUTUBER) && (tag == Tag.ELITE || tag == Tag.BETA)) continue;
            if (group == Group.HELPER && (tag == Tag.YOUTUBER || tag == Tag.STREAMER)) continue;

            if (group.ordinal() <= tag.getGroup().ordinal())
                tags.add(tag);
        }

        return tags;
    }

    public void setGroup(Group group, long time) {
        this.group = group;
        this.groupTime = time;

        if (getPlayer() != null && team == null) {
            if (group != null) {
                Tag tag = group.getTag();
                if (tag != null)
                    setTag(tag);
            }
        }
    }

    public void setState(GamerState state) {
        this.state = state;

        new PlayerChangeStateEvent(this, state).call();
    }

    public void setTeam(GamerTeam team) {
        this.team = team;

        if (team != null)
            setTag(Tag.valueOf(team.name()));
    }

    public boolean isFighting() {
        return fighting > 0;
    }

    public Player getPlayer() {
        if (player == null)
            player = Bukkit.getPlayer(uniqueId);

        return player;
    }

    public void addKill() {
        kills++;
    }

    public void updatePlayer() {
        if (getPlayer() == null) return;
        if (!isVanish()) {
            for (Gamer g : Main.getInstance().getGamerProvider().getGamers()) {
                if (g != null && g.getPlayer() != null) {
                    if (isAlive()) {
                        if (spectators) {
                            if (!g.isVanish())
                                player.showPlayer(g.getPlayer());
                        } else {
                            if (g.isAlive() && !g.isVanish())
                                player.showPlayer(g.getPlayer());
                            else if (g.isInvisible() || !g.isAlive())
                                player.hidePlayer(g.getPlayer());
                        }
                    } else {
                        if (!g.isVanish()){
                            player.showPlayer(g.getPlayer());
                        }
                    }
                }
            }
        } else {
            for (Gamer g : Main.getInstance().getGamerProvider().getGamers()) {
                if (g != null && g.getPlayer() != null) {
                    if (spectators) {
                        if (g.isInvisible()) continue;
                        player.showPlayer(g.getPlayer());
                    } else {
                        if (g.isAlive())
                            player.showPlayer(g.getPlayer());
                        else
                            player.hidePlayer(g.getPlayer());
                    }
                }
            }
        }
    }

    public void sendActionBarMessage(String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendTitle(String title, String subTitle){
        TitleAPI.send(player, title, subTitle, 20, 20, 10);
    }

    public String getKitName() {
        return kit == null ? "Nenhum" : kit.getName();
    }

    public void add(Inventory inventory, Material material, Location fallback, ItemStack itemStack) {
        if (firstPartial(inventory, material) == -1) {
            if (inventory.firstEmpty() == -1) {
                fallback.getWorld().dropItemNaturally(fallback.clone().add(0.0, 0.1, 0.0), itemStack);
            } else {
                inventory.addItem(itemStack);
            }
        } else {
            inventory.addItem(itemStack);
        }
    }

    public int firstPartial(Inventory inv, Material material) {
        ItemStack[] inventory = inv.getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getType() == material && item.getAmount() < (item.getType().toString().contains("MUSHROOM") ? 64 : item.getMaxStackSize())) {
                return i;
            }
        }
        return -1;
    }

    public void sendLogMessage(LogType type, String player, String player2){
        String message = ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + type.getMessage() + "]";

        message = message.replaceAll("%player1%", player);

        if (player2 != null)
            message = message.replaceAll("%player2%", player2);

        sendMessage(message);
    }

    public void ban(String reason, long time) {
        setBanned(true);
        setBanReason(reason);
        setBanTime(time);
    }

    public void unban() {
        setBanned(false);
        setBanReason(null);
        setBanTime(0L);
        Main.getInstance().getGamerRepository().update(this);
    }

    public void mute(String reason, long time) {
        setMuted(true);
        setMuteReason(reason);
        setMuteTime(time);
    }

    public void unmute() {
        setMuted(false);
        setMuteReason(null);
        setMuteTime(0L);
        Main.getInstance().getGamerProvider().update(this);
    }

    public static Gamer getGamer(UUID uniqueId) {
        return Main.getInstance().getGamerProvider().search(uniqueId);
    }
}
