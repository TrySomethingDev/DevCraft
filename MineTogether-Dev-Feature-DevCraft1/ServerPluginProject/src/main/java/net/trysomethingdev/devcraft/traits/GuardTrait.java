package net.trysomethingdev.devcraft.traits;

import io.papermc.paper.math.Position;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("guardtrait")
public class GuardTrait extends Trait {


    private int timeElapsed;

    public GuardTrait() {
        super("guardtrait");
    }

    DevCraftPlugin plugin = null;

    boolean SomeSetting = false;

    // see the 'Persistence API' section
    @Persist("mysettingname")
    boolean automaticallyPersistedSetting = false;

    @Persist("targettoattack")
    public Entity targetToAttack;


    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getEntity() will return null.
    public void load(DataKey key) {
        SomeSetting = key.getBoolean("SomeSetting", false);
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        key.setBoolean("SomeSetting", SomeSetting);
    }

    // An example event handler. All traits will be registered automatically as Spigot event Listeners
    @EventHandler
    public void click(NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (event.getNPC() == this.getNPC()) {
            Bukkit.getLogger().info("NPC CLICKED ON - Guard Trait");

        }
    }

//    @EventHandler
//    public void onMobTargetPlayer(EntityTargetLivingEntityEvent event) {
//        // Check if the target is a player
//        if (event.getTarget() instanceof Player) {
//            Player player = (Player) event.getTarget();
//            if (event.getEntityType().equals(LivingEntity.class)) {
//                LivingEntity mob = (LivingEntity) event.getEntity();
//                // Log or take action
//                player.sendMessage("A " + mob.getType() + " is now targeting you!");
//                Bukkit.getLogger().info(mob.getType() + " is targeting player " + player.getName());
//
//            }
//
//
//        }
//    }

    // NPC Gets Targeted
    @EventHandler
    public void onNPCTargeted(EntityTargetEvent event) {
        if (npc == null || !npc.isSpawned()) return;

        if (event.getTarget() != null && event.getTarget().equals(npc.getEntity())) {
            Bukkit.getLogger().info(npc.getName() + " is being targeted by " + event.getEntity().getName());
        }
    }
    private Flocker flock;
    private int rotation = 0;

    @Override
    public void onSpawn() {
        flock = new Flocker(npc, new RadiusNPCFlock(10), new SeparationBehavior(100));
    }

    @Override
    public void onDespawn() {
        flock = null;
    }
    @Override
    public void run() {

        Log("Top");
        if (!npc.isSpawned()) return;

        if (npc.getNavigator().isNavigating()) {
            Log("IsNavigating");
            return;
        }

        if (timeElapsed < 20) {
            timeElapsed++;
            return;
        }

        targetToAttack = null;
        timeElapsed = 0;

//        if ((targetToAttack = TryGetNewTarget()) != null && !targetToAtack.isDead())
//        { ... fight logit here }
//        else
//        { ... navigating logic here }
        targetToAttack = TryToGetNewTarget();

        if (targetToAttack == null) {

            //Need a new target
            targetToAttack = TryToGetNewTarget();
            if (targetToAttack == null) {
                //Follow player
                npc.getNavigator().setTarget(Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName()).getLocation());flock.run();
                return;
            }

        }

        Log("We have a target");

        Bukkit.getLogger().info(npc.getName() + " is attaching " + targetToAttack.getName());

        npc.faceLocation(targetToAttack.getLocation());

        npc.getNavigator().setTarget(targetToAttack, true);


    }


    private Entity TryToGetNewTarget() {

        Log("TryingToGetNewTarget");
        // Get aggressive mobs within a 5-block radius
        List<Monster> aggressiveMobs = getAggressiveMobsNearPlayer(10.0);

        // Do something with the nearby mobs (e.g., make them attack the player or attack NPCs)
        double closestMonsterDistance = 1000;
        Monster closestMonster = null;
        for (Monster monster : aggressiveMobs) {
            // For example, make each mob target the player
            var distance = npc.getEntity().getLocation().distance(monster.getLocation());
            if (distance < closestMonsterDistance) {
                closestMonsterDistance = distance;
                closestMonster = monster;
                if (distance < 1.5) {
                    return closestMonster;
                }
            }

        }

       return closestMonster;
    }


    @EventHandler
    public void onNPCDeath(EntityDeathEvent event) {
        if (npc == null || !npc.isSpawned()) return;

        npc.removeTrait(GuardTrait.class);

        if (event.getEntity().equals(npc.getEntity())) {
            Bukkit.getLogger().info(npc.getName() + " has died!");
        }
    }

    public List<Monster> getAggressiveMobsNearPlayer(double radius) {
        List<Monster> nearbyAggressiveMobs = new ArrayList<>();

        // Get all entities within the player's world
        for (Entity entity : npc.getEntity().getWorld().getEntities()) {
            // Check if the entity is a monster (hostile mob)
            if (entity instanceof Monster) {
                Monster monster = (Monster) entity;

                // Check the distance to the player
                if (entity.getLocation().distance(npc.getEntity().getLocation()) <= radius) {
                    nearbyAggressiveMobs.add(monster);
                }
            }
        }

        return nearbyAggressiveMobs;
    }

    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }


    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        //     plugin.getServer().getLogger().info(npc.getName() + "has been assigned MyTrait!");
        //       Bukkit.dispatchCommand(npc.getEntity(),"say I have a new trait.");
    }



    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

}



