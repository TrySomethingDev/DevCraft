package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("dancetrait")
    public class DanceTrait extends BaseTrait {

    private int jumpDelay;

    public DanceTrait() {
        super("dancetrait");
       }

        DevCraftPlugin plugin = null;

        boolean SomeSetting = false;

        // see the 'Persistence API' section
        @Persist("mysettingname") boolean automaticallyPersistedSetting = false;

        int length = 1;
        int width = 1;
        int depth = 1;

        int currentDepth = 0;

        int maxSize = 10;
    public DanceTrait(int length, int width, int depth) {
        super("dancetrait");



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

        private int tickCounter = 1;

    private int rotation = 0;


        @Override
        public void run() {
            
            if (!npc.isSpawned())  return;

            if(npc.getNavigator().isNavigating())
            {
                return;
            }

            rotation = (rotation + 10) % 360;
            npc.faceLocation(npc.getEntity().getLocation().add(Math.cos(Math.toRadians(rotation)), 0, Math.sin(Math.toRadians(rotation))));

            if (jumpDelay <= 0) {
                LivingEntity entity = (LivingEntity) npc.getEntity();
                entity.setVelocity(entity.getVelocity().setY(1));  // Makes the NPC jump
                jumpDelay = 120;  // Makes the NPC jump once per second
            } else {
                jumpDelay--;
            }

        }





    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }

    private void NPCJump() {
        new DelayedTask(() -> {
            npc.getEntity().setVelocity(new Vector(0,1f,0));

        }, 20 * 1);
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



