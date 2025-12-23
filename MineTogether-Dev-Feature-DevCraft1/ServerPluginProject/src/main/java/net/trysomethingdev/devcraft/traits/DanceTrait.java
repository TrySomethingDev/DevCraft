package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import net.citizensnpcs.util.PlayerAnimation;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("dance")
    public class DanceTrait extends Trait {

    private static final int JUMP_COOLDOWN_TICKS = 35;
    private static final int ARM_SWING_TICKS = 8;
    private static final double CIRCLE_RADIUS = 0.8;
    private static final double ROTATION_SPEED_DEGREES = 8.0;

    private int jumpDelay;
    private int armSwingDelay;
    private double angleDegrees;

    public DanceTrait() {
        super("dance");
       }

        boolean SomeSetting = false;

        // see the 'Persistence API' section
        @Persist("mysettingname") boolean automaticallyPersistedSetting = false;

        int length = 1;
        int width = 1;
        int depth = 1;

        int maxSize = 10;
    public DanceTrait(int length, int width, int depth) {
        super("dance");



    }

    // Here you should load up any values you have previously saved (optional).
        // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
        // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
        // This is called BEFORE onSpawn, npc.getEntity() will return null.
        public void load(DataKey key) {
            SomeSetting = key.getBoolean("SomeSetting", false);
        }

        // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
        public void save(DataKey key) {
            key.setBoolean("SomeSetting",SomeSetting);
        }

        // An example event handler. All traits will be registered automatically as Spigot event Listeners
        @EventHandler
        public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
            //Handle a click on a NPC. The event has a getNPC() method.
            //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
            if(event.getNPC() == this.getNPC() )
            {
               Bukkit.getLogger().info("NPC CLICKED ON - Dance");
                NPCJump();
            }
        }

        @Override
        public void run() {
            if (!npc.isSpawned())  return;

            if(npc.getNavigator().isNavigating())
            {
                return;
            }

            angleDegrees = (angleDegrees + ROTATION_SPEED_DEGREES) % 360;
            Location center = npc.getEntity().getLocation();
            double radians = Math.toRadians(angleDegrees);
            double offsetX = Math.cos(radians) * CIRCLE_RADIUS;
            double offsetZ = Math.sin(radians) * CIRCLE_RADIUS;
            Location target = center.clone().add(offsetX, 0, offsetZ);
            npc.faceLocation(target);

            Vector forward = target.toVector().subtract(center.toVector()).normalize().multiply(0.25);
            npc.getEntity().setVelocity(forward);

            if (npc.getEntity() instanceof Player) {
                if (armSwingDelay-- <= 0) {
                    PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                    armSwingDelay = ARM_SWING_TICKS;
                }
            }

            if (jumpDelay-- <= 0) {
                LivingEntity entity = (LivingEntity) npc.getEntity();
                entity.setVelocity(entity.getVelocity().setY(0.5));  // Hop for rhythm
                jumpDelay = JUMP_COOLDOWN_TICKS;
            }

        }





    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }

    private void NPCJump() {
        npc.getEntity().setVelocity(new Vector(0,0.6f,0));
       }



    //Run code when your trait is attached to a NPC.
        //This is called BEFORE onSpawn, so npc.getEntity() will return null
        //This would be a good place to load configurable defaults for new NPCs.
        @Override
        public void onAttach() {
       //     plugin.getServer().getLogger().info(npc.getName() + "has been assigned MyTrait!");
     //       Bukkit.dispatchCommand(npc.getEntity(),"say I have a new trait.");
        }

        // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
        @Override
        public void onDespawn() {
       //     Bukkit.dispatchCommand(npc.getEntity(),"say Hi I have unloaded.");
        }

        //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
        //This is called AFTER onAttach and AFTER Load when the server is started.
        @Override
        public void onSpawn() {
        //    Bukkit.dispatchCommand(npc.getEntity(),"say Hi I have loaded.");
        }

        //run code when the NPC is removed. Use this to tear down any repeating tasks.
        @Override
        public void onRemove() {
        }

    }


