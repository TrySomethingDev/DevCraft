package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("unload")
    public class UnloadTrait extends Trait {

    private int jumpDelay;

    public UnloadTrait() {
        super("unload");
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
    public UnloadTrait(int length, int width, int depth) {
        super("unload");



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
               Bukkit.getLogger().info("NPC CLICKED ON - Unload");
                NPCJump();
            }
        }

        private int tickCounter = 1;

    private int rotation = 0;


        @Override
        public void run() {

            if (!npc.isSpawned()) return;

            if (npc.getNavigator().isNavigating()) {
                return;
            }

            //If we have no inventory...
            //Just remove trait and return.
            var inv = npc.getOrAddTrait(Inventory.class);
            var contents = inv.getContents();

            boolean hasInventory = CheckIfNPCHasInventory(contents);

            //If no inventory then remove the trait.
            if (!hasInventory) {
                npc.removeTrait(UnloadTrait.class);
                Log("Removing Unload Trait");
                return;
            }

                Log("Searching....");
                //Find closest Chest
            var result = SearchForMaterialInRaidus(npc.getEntity().getLocation(),30,-3,3,Material.CHEST);

            if(result != null)
            {
                Log(result.toString());
            }







//                Log("Count is: " + Arrays.stream(inv.getContents()).count());
//
//                for (var content : contents){
//                    if(content == null)
//                    {
//                        //Log("Nothing");
//                    }
//                    else {
//                        Log(content.toString());
//                    }
//                }
            }
    private static Block SearchForMaterialInRaidus(Location location, int radius, int ylower, int yhigher, Material blockTypeWeAreLookingFor) {
        // ylower is how much lower than the current Y we should search.
        // yhigher is how much above the current Y we should search

        Block closestBlockOfSpecifiedMaterial = null;
        double closestDistance = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = ylower; y <= yhigher; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                    if (block.getType() == blockTypeWeAreLookingFor) {

                        double distance = block.getLocation().distance(location);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestBlockOfSpecifiedMaterial = block;
                        }



                    }
                }
            }
        }
        return closestBlockOfSpecifiedMaterial;
    }
    private static boolean CheckIfNPCHasInventory(ItemStack[] contents) {
        boolean hasInventory = false;
        for(var content : contents)
        {
            if(content != null){
                hasInventory = true;
                break;
            }
        }
        return hasInventory;
    }


    //Find a nearby chest.

            //Does this chest have any available space in it?

            //If it does then navigate to the chest.

            //Once we arrive at the chest
            //Place what we can in the chest...

            //IF the chest is full then we want to find another chest...
            //Repeat process until inventory is empty, and then
            // remove trait.







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



