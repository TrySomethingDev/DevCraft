package net.trysomethingdev.devcraft.traits;

import com.denizenscript.denizen.npc.traits.SleepingTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("gotobedtrait")
    public class GoToBedTrait extends Trait {


    public GoToBedTrait() {
            super("gotobedtrait");
            plugin = JavaPlugin.getPlugin(DevCraftPlugin.class);
        }

        DevCraftPlugin plugin = null;

        boolean SomeSetting = false;

        // see the 'Persistence API' section
        @Persist("mysettingname") boolean automaticallyPersistedSetting = false;

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
               Bukkit.getLogger().info("NPC CLICKED ON");
            }
        }

private Boolean isSleeping = false;
 private int tickCounter = 1;
        @Override
        public void run() {
            tickCounter++;
            if(tickCounter % 100 == 0) {
                tickCounter = 0;

                if(npc.getNavigator().isNavigating())
                {
                    var trait = npc.getOrAddTrait(SleepingTrait.class);
                    trait.wakeUp();
                    return;
                }

                new DelayedTask(() -> {
                    var bedLocation = findNearestBed(npc.getEntity().getLocation());
                    var trait = npc.getOrAddTrait(SleepingTrait.class);

                    if(npc.getEntity().getLocation().distance(bedLocation) < 5 )
                    {

                        trait.toSleep(bedLocation);
                        isSleeping = true;

                    }
                    else
                    {

                        trait.wakeUp();

                        isSleeping = false;
                    }

                    if(!isSleeping)
                    {
                        if (bedLocation != null) {
                            Log("Ticks" + tickCounter);
                            Navigator navigator = npc.getNavigator();
                            navigator.setTarget(bedLocation);
                        }
                    }

                }, 20 * 1);

            }


        }

    private Location findNearestBed(Location startingLocation) {



        Material blockTypeWeAreLookingFor = Material.RED_BED;
        int radius = 100; // Define the radius for the search

        var closestBed = SearchForMaterialInRaidus(startingLocation, radius, blockTypeWeAreLookingFor);

        return closestBed != null ? closestBed.getLocation() : null;
        }

    private static Block SearchForMaterialInRaidus(Location location, int radius, Material blockTypeWeAreLookingFor) {
        Block closestBlockOfSpecifiedMaterial = null;
        double closestDistance = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
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


    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }

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



