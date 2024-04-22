package net.trysomethingdev.devcraft.traits;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

public class FollowTraitCustom extends Trait {

    @Persist("active")
    private boolean enabled = true;
    private Flocker flock;
    @Persist
    private UUID followingUUID;
    private Player player;
    @Persist
    private boolean protect;

    @Persist
    private double margin = 5;
    public FollowTraitCustom() {

        super("followtraitcustom");
        player = Bukkit.getPlayer("TrySomethingDev");
    }





    public FollowTraitCustom(Player player) {
        super("followtraitcustom");
        this.player = player;
    }
    public FollowTraitCustom(Player player, double margin) {
        super("followtraitcustom");
        this.player = player;

        this.margin = margin *  ThreadLocalRandom.current().nextDouble(0.5, 1.0);;
    }

    public FollowTraitCustom(Player player, boolean protect) {
        super("followtraitcustom");
        this.player = player;
    }

    public Player getFollowingPlayer() {
        return player;
    }

    /**
     * Returns whether the trait is actively following a {@link Player}.
     */
    public boolean isActive() {
        return enabled && npc.isSpawned() && player != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onDespawn() {
        flock = null;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (isActive() && protect && event.getEntity().equals(player)) {
            Entity damager = event.getDamager();
            if (event.getEntity() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getEntity();
                if (projectile.getShooter() instanceof Entity) {
                    damager = (Entity) projectile.getShooter();
                }
            }
            npc.getNavigator().setTarget(damager, true);
        }
    }

    @Override
    public void onSpawn() {
        flock = new Flocker(npc, new RadiusNPCFlock(10), new SeparationBehavior(1000));
    }

    @Override
    public void run() {
        Bukkit.broadcastMessage("Follow Trait Custom is running");
        if (player == null || !player.isValid()) {
            if (followingUUID == null)
                return;
            player = Bukkit.getPlayer(followingUUID);
            if (player == null) {
                return;
            }
        }
        if (!isActive()) {
            return;
        }
        if (!npc.getEntity().getWorld().equals(player.getWorld())) {
            npc.teleport(player.getLocation(), TeleportCause.PLUGIN);
            return;
        }
        if (!npc.getNavigator().isNavigating() && npc.getEntity().getLocation().distance(player.getLocation()) >= margin) {
                npc.getNavigator().setTarget(player.getLocation());

        } else {
            if (npc.getEntity().getLocation().distance(player.getLocation()) <= margin)
            {
                npc.getNavigator().cancelNavigation();
            }
            flock.run();
        }
    }

    /**
     * Toggles and/or sets the {@link OfflinePlayer} to follow and whether to protect them (similar to wolves in
     * Minecraft, attack whoever attacks the player).
     * <p>
     * Will toggle if the {@link OfflinePlayer} is the player currently being followed.
     *
     * @param player  the player to follow
     * @param protect whether to protect the player
     * @return whether the trait is enabled
     */
    public boolean toggle(OfflinePlayer player, boolean protect) {
        this.protect = protect;
        if (player.getUniqueId().equals(this.followingUUID) || this.followingUUID == null) {
            this.enabled = !enabled;
        }
        this.followingUUID = player.getUniqueId();
        if (npc.getNavigator().isNavigating() && this.player != null && npc.getNavigator().getEntityTarget() != null
                && this.player == npc.getNavigator().getEntityTarget().getTarget()) {
            npc.getNavigator().cancelNavigation();
        }
        this.player = null;
        return this.enabled;
    }
}