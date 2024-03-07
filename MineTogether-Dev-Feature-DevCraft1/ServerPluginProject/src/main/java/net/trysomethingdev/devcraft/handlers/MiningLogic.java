package net.trysomethingdev.devcraft.handlers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.TrySomethingDevAmazingPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


public class MiningLogic {


    public static void StartMining(BlockPlaceEvent event, Player player, Material block, TrySomethingDevAmazingPlugin plugin) {

        //Get Nearest NPC
        NPC npc = GetNearestNPCToBlock(event.getBlock());


        ScheduleTaskToKeepMiningUntilWeReachNegative15(npc,player,plugin);

        //Have them come to the place where the block was placed.
        //npc.getNavigator().setTarget(event.getBlock().getLocation().add(1.5,0,0.5));
        //Have them start mining North and down to  level -15.

        // mine forward eye level

        //npc.getEntity().getWorld().getBlockAt(startingLocation);
        //mine forward foot level
        //mine forward one below foot level

        //Once down to level -15 create strip mine 100 block long
        //go 100 blocks left
        //go 100 block right every 2 blocks
        //Place inventory in chests.
        //Place

    }

    private static void MineForwardAndDown(NPC npc, Player player, TrySomethingDevAmazingPlugin plugin) {
        // Get the NPC's location and direction
        Location location = npc.getEntity().getLocation();
        Vector direction = location.getDirection();

        Block eyeBlock = MineEyeLevelFootLevelAndBelowFootLevelInFrontOfNPC(location, direction);
    }

    private static void MoveNPCForwardOneBlock(NPC npc,Player player,TrySomethingDevAmazingPlugin plugin) {
        // Move the NPC forward
        var locationToNavigateTo = npc.getEntity().getLocation().add(0, 0, 2);

        npc.getEntity().setVelocity(new Vector(0,0,1));
        npc.getNavigator().setTarget(locationToNavigateTo);
      //  Bukkit.dispatchCommand(player,"t");


        if (npc.getNavigator().canNavigateTo(locationToNavigateTo)) {
            Bukkit.broadcastMessage("We can reach location: " + locationToNavigateTo.toString());
            npc.getNavigator().setTarget(locationToNavigateTo);
            npc.getNavigator().getDefaultParameters().baseSpeed(1.0f);
        } else {
            Bukkit.broadcastMessage("We cannot reach location: " + locationToNavigateTo.toString());
        }

        ScheduleTaskToSeeIfWeHaveArrived(npc, locationToNavigateTo, player,plugin);
    }

    private static void ScheduleTaskToSeeIfWeHaveArrived(NPC finalNearestNPC, Location locationToNavigateTo,Player player,TrySomethingDevAmazingPlugin plugin) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler = Bukkit.getScheduler();
        BukkitScheduler finalScheduler = scheduler;
        scheduler.runTaskLater(plugin, (x) -> {

            if (locationToNavigateTo == null) {
                x.cancel();
                return;
            }

            if (finalNearestNPC.getNavigator().isNavigating()) {
                //Wait
                Bukkit.broadcastMessage("NPC Is travelling!");
                ScheduleTaskToSeeIfWeHaveArrived(finalNearestNPC, locationToNavigateTo, player, plugin);
            } else {

                Bukkit.broadcastMessage("NPC has stopped travelling");


                var currentLocationOfNPC = finalNearestNPC.getEntity().getLocation();

                //getcurrent location
                var startingLocation = finalNearestNPC.getEntity().getLocation();
                if (finalNearestNPC != null) {
                    MineForwardAndDown(finalNearestNPC,player,plugin);
                }
//                var oakLogLocation = oakLog.getLocation();
//
//                var distance = currentLocationOfNPC.distance(oakLogLocation);
//
//                if(distance > 5){
//                    Bukkit.broadcastMessage("NPC is too far from the log, cannot reach");
//                }
//                else {
//                    Bukkit.broadcastMessage("NPC has arrived at Destination!");
//                    finalNearestNPC.faceLocation(oakLog.getLocation());
//
//

//

            }


//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I am adding it to my inventory ");
//
//                        var itemStackArray = finalNearestNPC.getTrait(Inventory.class).getContents();
//                        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "say I see" + itemStackArray[1].toString() + " in array 1");
//
//                        itemStackArray[1].add(1);
//
//                        //finalNearestNPC.getTrait(Inventory.class).setItem(1,new ItemStack(Material.OAK_LOG));


        }, 20L * 1L /*<-- the delay */);

    }

    private static void ScheduleTaskToKeepMiningUntilWeReachNegative15(NPC finalNearestNPC,Player player,TrySomethingDevAmazingPlugin plugin) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler = Bukkit.getScheduler();
        BukkitScheduler finalScheduler = scheduler;
        scheduler.runTaskLater(plugin, (x) -> {



            if(finalNearestNPC.getEntity().getLocation().getY() <= -15)
            {
                x.cancel();
                return;
            }


                if (finalNearestNPC != null) {
                    MoveNPCForwardOneBlock(finalNearestNPC,player,plugin);
                }

        }, 20L * 1L /*<-- the delay */);

    }

        @NotNull
    private static Block MineEyeLevelFootLevelAndBelowFootLevelInFrontOfNPC(Location location, Vector direction) {
        // Calculate the block in front of the NPC at eye level
        Block eyeBlock = location.add(direction).getBlock();
        Block footBlock = eyeBlock.getRelative(0, 0, 0);
        Block belowFootBlock = footBlock.getRelative(0, -1, 0);

        // Break the blocks
        eyeBlock.setType(Material.AIR);
        footBlock.setType(Material.AIR);
        belowFootBlock.setType(Material.AIR);


//
//            // Break the blocks
//            eyeBlock.getRelative(BlockFace.SOUTH).setType(Material.AIR);
//            footBlock.getRelative(BlockFace.SOUTH).setType(Material.AIR);
//            belowFootBlock.getRelative(BlockFace.SOUTH).setType(Material.AIR);


        return eyeBlock;
    }

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

//    public void breakBlockAfterMoving(Player player, Location targetLocation) {
//        // Get the NPC from Citizens
//        NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
//
//        if (npc != null) {
//            // Set the NPC's target location
//            npc.getNavigator().setTarget(targetLocation);
//
//
////            // Create a new PathStrategy that waits until the NPC has reached its target
////            PathStrategy strategy = new PathStrategy() {
////                @Override
////                public boolean update() {
////                    // Check if the NPC has reached its target
////                    if (npc.getEntity().getLocation().distance(targetLocation) < 1.0) {
////                        // Get the block in front of the NPC
////                        Vector direction = npc.getEntity().getLocation().getDirection();
////                        Block block = npc.getEntity().getLocation().add(direction).getBlock();
////
////                        // Break the block
////                        block.setType(Material.AIR);
////
////                        // Return false to indicate that the strategy is complete
////                        return false;
////                    }
////
////                    // Return true to continue the strategy
////                    return true;
////                }
////
////                @Override
////                public void stop() {
////                    // This method is called when the strategy is stopped
////                }
////
////                @Override
////                public void clear() {
////                    // This method is called when the strategy is cleared
////                }
////            };
////
////
////            // Set the NPC's path strategy
////            npc.getNavigator().getDefaultParameters().pathStrategy(strategy);;
////        }
//    }
}
