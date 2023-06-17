package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.utils.ItemCreator;
import br.com.stenox.cxc.Main;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Grappler extends Kit {

    private HashMap<Player, GrapplerHook> hooks = new HashMap<Player, GrapplerHook>();
    private HashMap<Player, Long> left = new HashMap<Player, Long>();
    private HashMap<Player, Long> right = new HashMap<Player, Long>();


    public static double x = 7.0;

    public Grappler(GameManager gameManager) {
        super(gameManager);
        setCooldownSeconds(6.0D);
        setIcon(new ItemCreator().setType(Material.LEASH).getStack());
        setItems(new ItemCreator().setType(Material.LEASH).setName("§aGrappler").getStack());
        setDescription("§7Use sua corda para se locomover rapidamente pelo mapa.");
    }

    @EventHandler
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        if (this.hooks.containsKey(player)) {
            this.hooks.remove(player).remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.hooks.containsKey(player)) {
            this.hooks.remove(player).remove();
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (this.hooks.containsKey(player)) {
            this.hooks.remove(player).remove();
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            final Player player = (Player) event.getEntity();
            if (this.hooks.containsKey(player) && this.hooks.get(player).isHooked()) {
                event.setDamage(Math.min(3.0, event.getDamage()));
            }
        }
    }

    @EventHandler
    public void dano(final EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("corda")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void grappler(final PlayerLeashEntityEvent e) {
        final Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.LEASH && hasKit(p)) {
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }
    }

    public static double ovo = 0.1;

    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.hasItem() && isKitItem(e.getItem(), "§aGrappler") && hasKit(p)) {
            e.setCancelled(true);
            if (inCooldown(p)) {
                sendCooldown(p);
                return;
            }
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (this.left.containsKey(p) && this.left.get(p) > System.currentTimeMillis()) {
                    return;
                }
                if (this.hooks.containsKey(p)) {
                    this.hooks.get(p).remove();
                }
                this.left.put(p, System.currentTimeMillis() + 250L);
                final GrapplerHook nmsHook = new GrapplerHook(((CraftPlayer) p).getHandle());
                nmsHook.spawn(p.getEyeLocation().clone().add(p.getLocation().getDirection().getX(), p.getLocation().getDirection().getY(),
                        p.getLocation().getDirection().getZ()));
                nmsHook.move(p.getLocation().getDirection().getX() * 7.0D, p.getLocation().getDirection().getY() * 7.0D,
                        p.getLocation().getDirection().getZ() * 7.0D);
                this.hooks.put(p, nmsHook);
            } else {
                if (!this.hooks.containsKey(p)) {
                    return;
                }
                if (this.right.containsKey(p) && this.right.get(p) > System.currentTimeMillis()) {
                    return;
                }
                final GrapplerHook hook = this.hooks.get(p);
                if (!hook.isHooked()) {
                    hook.move(hook.motX, hook.motY - 2.0D, hook.motY);
                    return;
                }
                if (hook.locY > 128.0 && !Gladiator.gladiatorA.containsKey(p)) {
                    return;
                }
                this.right.put(p, System.currentTimeMillis() + 125L);
                if (hook.getHooked() instanceof LivingEntity) {
                    double d = this.hooks.get(p).getBukkitEntity().getLocation().distance(p.getLocation());
                    double t = d;
                    double v_x = (1.0D + 0.10000000000000001D * t)
                            * (this.hooks.get(p).getBukkitEntity().getLocation().getX() - p.getLocation().getX()) / t;

                    double v_z = (1.0D + 0.10000000000000001D * t)
                            * (this.hooks.get(p).getBukkitEntity().getLocation().getZ() - p.getLocation().getZ()) / t;
                    if (p.isOnGround()) {
                        if (this.hooks.get(p).getBukkitEntity().getLocation().getY() - p.getLocation().getY() < 0.5D) {
                        }
                    }
                    double v_y;
                    if (hooks.get(p).getBukkitEntity().getLocation().getBlockY() < p.getLocation().getBlockY()) {
                        v_y = -1.0D;
                    } else {
                        v_y = (0.9D + 0.09D * t) * ((this.hooks.get(p).getBukkitEntity().getLocation().getY() - p.getLocation().getY()) / t);
                    }
                    Vector v = p.getVelocity();
                    v.setX(v_x);
                    v.setY(v_y + 0.05);
                    v.setZ(v_z);
                    p.setVelocity(v);
                } else {
                    double d = this.hooks.get(p).getBukkitEntity().getLocation().distance(p.getLocation());
                    double t = d;
                    double v_x = (1.0D + 0.03500000000000001D * t)
                            * (this.hooks.get(p).getBukkitEntity().getLocation().getX() - p.getLocation().getX()) / t;

                    double v_z = (1.0D + 0.03500000000000001D * t)
                            * (this.hooks.get(p).getBukkitEntity().getLocation().getZ() - p.getLocation().getZ()) / t;
                    if (p.isOnGround()) {
                        if (this.hooks.get(p).getBukkitEntity().getLocation().getY() - p.getLocation().getY() < 0.5D) {
                        }
                    }
                    double v_y;
                    if (hooks.get(p).getBukkitEntity().getLocation().getBlockY() < p.getLocation().getBlockY()) {
                        v_y = 0.1D;
                    } else {
                        v_y = (0.9D + 0.03D * t) * ((this.hooks.get(p).getBukkitEntity().getLocation().getY() - p.getLocation().getY()) / t);
                    }
                    Vector v = p.getVelocity();
                    v.setX(v_x);
                    v.setY(v_y + 0.05);
                    v.setZ(v_z);
                    p.setVelocity(v);
                    if (Math.round(hook.locY) > p.getLocation().getBlockY() && p.getFallDistance() > 20.0f) {
                        p.setVelocity(new Vector());
                        p.setNoDamageTicks(0);
                        p.damage(0.0);
                    }
                    p.setFallDistance(0.0f);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPvP(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (hasKit((Player) e.getEntity())) {
                addCooldown((Player) e.getEntity(), getCooldownSeconds());
            }
        }
    }

    public double getAdjusted(double val) {
        if (val < -4.0) {
            val = -4.0;
        }
        if (val > 4.0) {
            val = 4.0;
        }
        return val;
    }


    public class GrapplerHook extends EntityFishingHook {
        private Snowball sb;
        private EntitySnowball controller;
        private EntityHuman owner;
        private Entity hooked;
        private boolean lastControllerDead;
        private boolean isHooked;

        public GrapplerHook(final EntityHuman human) {
            super(human.world, human);
            this.owner = human;
        }

        public void t_() {
            if (!this.lastControllerDead && this.controller.dead) {
            } else {
                for (final Entity e : this.controller.getBukkitEntity().getNearbyEntities(0.5, 1.0, 0.5)) {
                    if (e instanceof Player && !Gamer.getGamer(e.getUniqueId()).isAlive())
                        continue;
                    if (!(e instanceof Firework) && !(e instanceof Item) && !(e instanceof Projectile) && e.getEntityId() != this.getBukkitEntity().getEntityId() && e.getEntityId() != this.owner.getBukkitEntity().getEntityId() && e.getEntityId() != this.controller.getBukkitEntity().getEntityId()) {
                        this.controller.die();
                        this.hooked = e;
                        this.isHooked = true;
                        Location loc = e.getLocation().clone().add(0, 0.5, 0);
                        this.locX = loc.getX();
                        this.locY = loc.getY();
                        this.locZ = loc.getZ();
                        this.motX = 0.0;
                        this.motY = 0.04;
                        this.motZ = 0.0;
                        break;
                    }
                }
            }
            this.lastControllerDead = this.controller.dead;
            try {
                this.locX = this.hooked.getLocation().getX();
                this.locY = this.hooked.getLocation().getY();
                this.locZ = this.hooked.getLocation().getZ();
                this.motX = 0.0;
                this.motY = 0.05;
                this.motZ = 0.0;
                this.isHooked = true;
            } catch (Exception e2) {
                if (this.controller.dead) {
                    this.isHooked = true;
                }
                this.locX = this.controller.locX;
                this.locY = this.controller.locY;
                this.locZ = this.controller.locZ;
            }
        }

        protected void c() {
        }

        public void die() {
        }

        public void spawn(final Location loc) {
            (this.sb = this.owner.getBukkitEntity().launchProjectile(Snowball.class)).setMetadata("corda", new FixedMetadataValue(Main.getInstance(), "Cordinha"));
            this.controller = ((CraftSnowball) this.sb).getHandle();
            this.sb.setVelocity(sb.getVelocity().multiply(1.42));
            final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
                    new int[]{this.controller.getId()});
            for (Player p : Bukkit.getOnlinePlayers()) {
                CraftPlayer bp = (CraftPlayer) p;
                bp.getHandle().playerConnection.sendPacket(packet);
                ((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
            }
        }

        public void remove() {
            super.die();
        }

        public boolean isHooked() {
            return this.isHooked;
        }

        public Entity getHooked() {
            return this.hooked;
        }

        public void setHookedEntity(final Entity nodamage) {
            this.hooked = nodamage;
        }
    }
}
