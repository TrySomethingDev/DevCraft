package net.trysomethingdev.devcraft.handlers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.util.PlayerAnimation;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ExperimentalHandler implements Listener {
    private final DevCraftPlugin _plugin;
    private NPC npc;

    public ExperimentalHandler(DevCraftPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        var npcHelper = new NpcHelper();
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        if (block == Material.TORCH) {


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
        }
        else if (block == Material.GOLD_BLOCK) {
            npcHelper.moveClosestNPCOneBlockPostiveX(event,_plugin);
        }
        else if (block == Material.DIAMOND_BLOCK) {
            npcHelper.moveClosestNPCOneBlockNegativeX(event,_plugin);
        }
        else if (block == Material.BONE_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            npc.getEntity().setVelocity(new Vector(1,0,0));

        }
        else if (block == Material.COAL_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            npc.getEntity().setVelocity(new Vector(0,0,0));

        }
        else if (block == Material.HAY_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            Bukkit.getLogger().info("Firing Hay Block placed event");
            findChestAndTransferItems(npc);

        }
        else if (block == Material.HONEY_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            Bukkit.getLogger().info("Firing Honey Block placed event");
            clearAllItemsFromNPCInventory(npc);

        }
        else if (block == Material.COPPER_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            Bukkit.getLogger().info("Firing Copper Block placed event");
            TransferAllItemsThatWillFitToChest(npc);

        }
        else if (block == Material.DRIED_KELP_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            Bukkit.getLogger().info("Firing Dried Kelp Block placed event");
            SpitOutOneInventory(npc);

        }
        else if (block == Material.MOSS_BLOCK) {
            var npc = GetNearestNPCToBlock(event.getBlock());
            Bukkit.getLogger().info("Firing Moss Block placed event");
            SpitOutAllInventory(npc);

        }
    }

    private void SpitOutAllInventory(NPC npc) {

            var npcInv = npc.getOrAddTrait(Inventory.class);
            var contents = npcInv.getInventoryView().getContents();
        for (int i = 0; i < contents.length; i++) {

            var itemStack = npcInv.getInventoryView().getItem(i);
            if(itemStack == null) continue;

            var drop = npc.getEntity().getWorld().dropItem(npc.getEntity().getLocation().add(0,1.5,0),itemStack);
            drop.setVelocity(npc.getEntity().getLocation().getDirection().multiply(.8));

            npcInv.setItem(i,new ItemStack(Material.STICK, 0));
        }
    }

    private void SpitOutOneInventory(NPC npc) {
      var drop = npc.getEntity().getWorld().dropItem(npc.getEntity().getLocation().add(0,1.5,0),new ItemStack(Material.DIAMOND,1));
      drop.setVelocity(npc.getEntity().getLocation().getDirection().multiply(.8));

    }

    private void TransferAllItemsThatWillFitToChest(NPC npc) {
        Block block = npc.getEntity().getWorld().getBlockAt(npc.getEntity().getLocation());

        Bukkit.getLogger().info(block.toString());
        if (block.getType() == Material.CHEST) {


            Chest chest = (Chest) block.getState();
            var chestInventory = chest.getInventory();
            var npcInv = npc.getOrAddTrait(Inventory.class);

            while(chestInventory.firstEmpty() != -1)
            {   Bukkit.getLogger().info("CHEST FIRST EMPTY VALUE");
                Bukkit.getLogger().info(String.valueOf(chestInventory.firstEmpty()));

                //Get the first location id with an ItemStack in it and distribute it into the other chest.
                ItemStack npcItemStack;
                for (int i = 0; i < npcInv.getContents().length; i++) {
                       npcItemStack = npcInv.getInventoryView().getItem(i);
                       if(npcItemStack == null) continue;
                }
            }
            for(int n = 0; n < 27; ++n) {
                ItemStack stack = npcInv.getInventoryView().getItem(n);;
                if(stack == null) continue;
                chestInventory.setItem(n, stack) ;

            }
        }
    }

    private void clearAllItemsFromNPCInventory(NPC npc) {


            var npcInv = npc.getOrAddTrait(Inventory.class);
            ItemStack[] itemStacks = new ItemStack[]{
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0),
                    new ItemStack(Material.STICK,0)

            };
            npcInv.setContents(itemStacks);
    }

    public void findChestAndTransferItems(NPC npc) {
        Block block = npc.getEntity().getWorld().getBlockAt(npc.getEntity().getLocation());

        Bukkit.getLogger().info(block.toString());
        if (block.getType() == Material.CHEST) {
            Chest chest = (Chest) block.getState();
            var chestInventory = chest.getInventory();
            var npcInv = npc.getOrAddTrait(Inventory.class);
            for(int n = 0; n < 27; ++n) {
                ItemStack stack = npcInv.getInventoryView().getItem(n);;
                if(stack == null) continue;
                chestInventory.setItem(n, stack) ;
            }
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


