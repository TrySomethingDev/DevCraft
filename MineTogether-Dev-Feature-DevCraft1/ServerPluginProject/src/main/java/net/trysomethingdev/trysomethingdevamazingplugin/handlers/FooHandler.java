package net.trysomethingdev.trysomethingdevamazingplugin.handlers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
import net.trysomethingdev.trysomethingdevamazingplugin.traits.MyTrait;
import net.trysomethingdev.trysomethingdevamazingplugin.util.NPCMovmentUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class FooHandler implements Listener {
    private final TrySomethingDevAmazingPlugin _plugin;
    private NPC npc;

    public FooHandler(TrySomethingDevAmazingPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        if (block == Material.TORCH) {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Chowmeinnnnnn");
            npc.spawn(event.getBlock().getLocation());

        } else if (block == Material.BLACK_WOOL) {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                npc.getNavigator().setTarget(player, true);
            }
        } else if (block == Material.WHITE_WOOL) {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                npc.getNavigator().cancelNavigation();
            }
        } else if (block == Material.BEDROCK) {
            CitizensAPI.getNPCRegistry().deregisterAll();
        }
         else if (block == Material.GREEN_WOOL) {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                npc.getTrait(net.citizensnpcs.api.trait.trait.Equipment.class).set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_AXE));
            }
        } else if (block == Material.YELLOW_WOOL) {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                npc.getTrait(net.citizensnpcs.api.trait.trait.Equipment.class).set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_PICKAXE));
            }
        } else if (block == Material.BLUE_WOOL) {
            FindNearestOakLogAndGetIt(player);
        } else if (block == Material.RED_WOOL) {
            MiningLogic.StartMining(event,player,block,_plugin);
        }
        else if (block == Material.GOLD_BLOCK) {
            NpcHelper.MoveClosestNPCOneBlockPostiveX(event,_plugin);
        }
        else if (block == Material.DIAMOND_BLOCK) {
            NpcHelper.MoveClosestNPCOneBlockNegativeX(event,_plugin);
        }
        else if (block == Material.BONE_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            npc.getEntity().setVelocity(new Vector(1,0,0));

        }
        else if (block == Material.COAL_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            npc.getEntity().setVelocity(new Vector(0,0,0));

        }
        else if (block == Material.EMERALD_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());

            var instanceOfTraitClass = new MyTrait();

            npc.addTrait(instanceOfTraitClass);

        }



    }

    private void HaveNPCSwingAxeFor3Seconds(NPC npc) {
        // Simulate the NPC swinging its arm for 3 seconds
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 60) { // 20 ticks = 1 second, so 60 ticks = 3 seconds
                    this.cancel();
                }
                PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                count++;
            }
        }.runTaskTimer(_plugin, 0, 1); // Replace "MyPlugin" with your main plugin class
    }

    private void FindNearestOakLogAndGetIt(Player player) {

        Block oakLog = findNearestOakLog(player);
        if(oakLog == null)
        {
            return;
        }
        Bukkit.broadcastMessage("OakBlockFoundAt: " + oakLog.getLocation().toString() );

        Block surfaceNextToBlock = findNextSurfaceBesidesBlock(oakLog);
        Bukkit.broadcastMessage("Surface next to Oak Log Found : " + surfaceNextToBlock.getLocation().toString() );
        if (oakLog != null) {
            NPC nearestNPC = GetNearestNPCToBlock(oakLog);
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

                ScheduleTaskToSeeIfWeHaveArrived(finalNearestNPC,oakLog,player);

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

    private void ScheduleTaskToSeeIfWeHaveArrived(NPC finalNearestNPC, Block oakLog,Player player) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler = Bukkit.getScheduler();
        BukkitScheduler finalScheduler = scheduler;
        scheduler.runTaskLater(_plugin, (x) -> {

            if(oakLog == null)
            {
                x.cancel();
                return;
            }

            if (finalNearestNPC.getNavigator().isNavigating()) {
                //Wait
                Bukkit.broadcastMessage("NPC Is travelling!");
                ScheduleTaskToSeeIfWeHaveArrived(finalNearestNPC, oakLog,player);
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
                                    FindNearestOakLogAndGetIt(player);
                                    this.cancel();
                                }
                                PlayerAnimation.ARM_SWING.play((Player) finalNearestNPC.getEntity());
                                count++;
                            }
                        }.runTaskTimer(_plugin, 0, 1); // Replace "MyPlugin" with your main plugin class


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



    public Block findNearestOakLog(Player player) {
        Block playerBlock = player.getLocation().getBlock();
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
}


