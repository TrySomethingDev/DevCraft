package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.util.PlayerAnimation;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("loggingtrees")
    public class LoggingTreesTrait extends Trait {
        public LoggingTreesTrait() {
            super("loggingtrees");
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
               Bukkit.getLogger().info("NPC CLICKED ON - Logging");
                NPCJump();
            }
        }

        private int tickCounter = 1;

        DevCraftTwitchUser user;
        private int delay;
        @Override
        public void run() {

            if (!npc.isSpawned() || delay-- > 0)
                return;


            delay = 20;

//            if(user == null)
//            {
//                //Assumption at this time...twitch username == npc.name
//              user = plugin.getTwitchUsersManager().getUserByTwitchUserName(npc.getName());
//              Log("Found Twitch User: " + user.twitchUserName);
//              if(user == null)
//              {
//                  //We have to have a twitch user.
//                  return;
//              }
//            }
         tickCounter++;

         if(hasEnded)
         {
             npc.removeTrait(this.getClass());
             return;
         }

            if(isFindingOakLog)
            {
                return;
            } else{
                 FindNearestOakLogAndGetIt() ;
            }

        }

    boolean isFindingOakLog = false;
        boolean hasEnded = false;
    private void FindNearestOakLogAndGetIt() {
        isFindingOakLog = true;

        Block oakLog = findNearestOakLog(npc);
        if(oakLog == null)
        {
            return;
        }
        Bukkit.broadcastMessage("OakBlockFoundAt: " + oakLog.getLocation().toString() );

        Block surfaceNextToBlock = findNextSurfaceBesidesBlock(oakLog);
        Bukkit.broadcastMessage("Surface next to Oak Log Found : " + surfaceNextToBlock.getLocation().toString() );

       if(oakLog == null) {
           isFindingOakLog = false;
           hasEnded = true;
           return;

       }
        if (oakLog != null) {
            NPC nearestNPC = npc;
            if (nearestNPC != null) {

                //We have the location of the floating Log
                //Now we need to get the location of ground next to it.

                var locationToNavigateTo = surfaceNextToBlock.getLocation();
                if (nearestNPC.getNavigator().canNavigateTo(locationToNavigateTo))
                {
                    Bukkit.broadcastMessage("We can reach location: " + locationToNavigateTo.toString() );
                    nearestNPC.getNavigator().setTarget(locationToNavigateTo);
                    nearestNPC.getNavigator().getDefaultParameters().baseSpeed(1.0f);
                }
                else
                {
                    Bukkit.broadcastMessage("We cannot reach location: " + locationToNavigateTo.toString() );
                }

                // When the NPC reaches the oak log, remove the block and add an oak log to the NPC's inventory

                NPC finalNearestNPC = nearestNPC;

                ScheduleTaskToSeeIfWeHaveArrived(finalNearestNPC,oakLog);

//
//                    nearestNPC.getNavigator().getDefaultParameters().addSingleUseCallback((NavigatorCallback) (callback) -> {
//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I am chopping the tree ");
//                        oakLog.setType(Material.AIR);
//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I am adding it to my inventory ");
//
//                        var itemStackArray = finalNearestNPC.getTrait(Inventory.class).getContents();
//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I see" + itemStackArray[1].toString() + " in array 1");
//
//                        itemStackArray[1].add(1);
//
//                        //finalNearestNPC.getTrait(Inventory.class).setItem(1,new ItemStack(Material.OAK_LOG));
//                    });
            }
        }
    }

    private Block findNextSurfaceBesidesBlock(Block block) {


        Block nextBlock = block.getRelative(-1,0,0);
        var startingY = nextBlock.getY();

        for (int y = startingY; y >= startingY - 10; y--) {



            if(nextBlock.getType() == Material.AIR || nextBlock.getType() == block.getBlockData().getMaterial()) {
                Bukkit.broadcastMessage("Have not found surface yet: " +  nextBlock.getLocation());
                nextBlock = nextBlock.getRelative(0,-1,0);
            } else {
                Bukkit.broadcastMessage("We found a surface: " + nextBlock.getLocation());

                return nextBlock;
            }

        }

        return null;

    }

    @Nullable
    private static NPC GetNearestNPCToBlock(Block block) {
        NPC nearestNPC = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            double distance = npc.getEntity().getLocation().distance(block.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNPC = npc;
            }
        }
        return nearestNPC;
    }

    private void ScheduleTaskToSeeIfWeHaveArrived(NPC finalNearestNPC, Block oakLog) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler = Bukkit.getScheduler();
        BukkitScheduler finalScheduler = scheduler;
        scheduler.runTaskLater(plugin, (x) -> {

            if(oakLog == null)
            {
                x.cancel();
                return;
            }

            if (finalNearestNPC.getNavigator().isNavigating()) {
                //Wait
                Bukkit.broadcastMessage("NPC Is travelling!");
                ScheduleTaskToSeeIfWeHaveArrived(finalNearestNPC, oakLog);
            } else {

                Bukkit.broadcastMessage("NPC has stopped travelling");

                var currentLocationOfNPC =  finalNearestNPC.getEntity().getLocation();


                var oakLogLocation = oakLog.getLocation();

                var distance = currentLocationOfNPC.distance(oakLogLocation);

                if(distance > 5){
                    Bukkit.broadcastMessage("NPC is too far from the log, cannot reach");
                }
                else {
                    Bukkit.broadcastMessage("NPC has arrived at Destination!");
                    finalNearestNPC.faceLocation(oakLog.getLocation());


                    // Simulate the NPC swinging its arm for 3 seconds
                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count >= 60) { // 20 ticks = 1 second, so 60 ticks = 3 seconds
                                if(oakLog == null)
                                {
                                    this.cancel();
                                    return;
                                }

                                oakLog.setType(Material.AIR);
                                FindNearestOakLogAndGetIt();
                                this.cancel();
                            }
                            PlayerAnimation.ARM_SWING.play((Player) finalNearestNPC.getEntity());
                            count++;
                        }
                    }.runTaskTimer(plugin, 0, 1); // Replace "MyPlugin" with your main plugin class


                }




//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I am adding it to my inventory ");
//
//                        var itemStackArray = finalNearestNPC.getTrait(Inventory.class).getContents();
//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I see" + itemStackArray[1].toString() + " in array 1");
//
//                        itemStackArray[1].add(1);
//
//                        //finalNearestNPC.getTrait(Inventory.class).setItem(1,new ItemStack(Material.OAK_LOG));

            }
        }, 20L * 1L /*<-- the delay */);


    }



    public Block findNearestOakLog(NPC npc) {
        Block playerBlock = npc.getEntity().getLocation().getBlock();
        for (int x = -10; x <= 10; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -10; z <= 10; z++) {
                    Block block = playerBlock.getRelative(x, y, z);
                    if (block.getType() == Material.OAK_LOG) {
                        return block;
                    }
                    else
                    {
                        //When debuggin we can replace the search path with gold.
                        // block.setType(Material.GOLD_BLOCK);
                    }
                }
            }
        }
        return null;
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



