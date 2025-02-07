package net.trysomethingdev.devcraft.traits;

import io.papermc.paper.entity.LookAnchor;
import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.util.PlayerAnimation;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;


public class M3Trait extends Trait {
    private Player owner;
    private boolean isMining = false;
    private static final int SEARCH_RADIUS = 4;
    private int delayCounter;
    private int stuckExecuting;
    private int blockBreakerTimeCounter;

    private static final int INVENTORY_LIMIT = 36;
    public M3Trait() {
        super("M3Trait");
    }

    public void Log(String message) {
        DevCraftPlugin.getInstance().getLogger().info(message);
    }

    @Override
    public void onAttach() {

        //  Bukkit.broadcastMessage("OnAttach");

        npc.data().setPersistent(NPC.Metadata.PICKUP_ITEMS, true);

        //   inventory = Bukkit.createInventory(null, 36); // Create a new inventory for the NPC
        var eq = npc.getOrAddTrait(Equipment.class);
        eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_PICKAXE));

    }

    private Flocker flock;


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

        if (delayCounter < 10) {

            delayCounter++;
            return;
        } else {
            // Log("Delay Counter Reset");
            delayCounter = 0;
        }

        if (owner == null) {
            Log("Setting Owner");
            owner = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName());
        }

        if (isInventoryFull()) {
            Log("Inventory is Full");
            depositInventory();
            return;
        }

        if (isMining) {
            Log("IsMining");
            return;
        }

        if (npc.getNavigator().isNavigating()) {
            Log("IsNavigating");
            return;
        }
        blockBreakerTimeCounter++;

        if (blockBreakerTimeCounter > 10) {
            Log("Canceling Current Execution");
            npc.getDefaultGoalController().cancelCurrentExecution();
        }



        if (isMining) {
            Log("IsMining");
            return;
        }

        if (npc.getNavigator().isNavigating()) {
            Log("IsNavigating");
            return;
        }

        flock.run();
        
        if (npc.getDefaultGoalController().isExecutingGoal()) {
            ///     Bukkit.broadcastMessage("ExecutingGoal");
            Log("IsExecutingGoal");
            Log(npc.getDefaultGoalController().toString());
            stuckExecuting++;
            if (stuckExecuting > 100) {
                //          Bukkit.broadcastMessage("Stuck Executing");
                Log("StuckExecuting Greater than 100");
            }
            return;
        } else {
            Log("StuckExecuting Counter Reset");
            stuckExecuting = 0;
        }


        Log("Running the Runnable");
        Block nearestOre = findNearestVisibleOre();
        if (nearestOre != null) {
            Log("Breaing The Blocks");
            BreakTheBlock(nearestOre);
        } else {
            Log("Navigating to Owner");
            npc.getEntity().lookAt(owner.getLocation(), LookAnchor.FEET);
            npc.getNavigator().setTarget(owner.getLocation());
        }

    }


    private Block findNearestVisibleOre() {
        Log("Finding Nearest Visable Ore");
        Location npcLocation = npc.getEntity().getLocation();
        World world = npcLocation.getWorld();
        PriorityQueue<Block> sortedOres = new PriorityQueue<>((b1, b2) ->
                Double.compare(npcLocation.distanceSquared(b1.getLocation()),
                        npcLocation.distanceSquared(b2.getLocation())));

        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    Block block = world.getBlockAt(npcLocation.clone().add(x, y, z));
                    if (isOre(block) && hasLineOfSight(npcLocation, block.getLocation())) {
                        Log("Found Ore in Line of Site");
                        sortedOres.add(block);
                    }
                }
            }
        }

        return sortedOres.poll();
    }

    private boolean hasLineOfSight(Location from, Location to) {
        Vector direction = to.toVector().subtract(from.toVector()).normalize();
        Location checkLocation = from.clone();
        double distance = from.distance(to);

        for (double i = 0; i < distance; i += 0.5) {
            checkLocation.add(direction.multiply(0.5));
            if (!checkLocation.getBlock().isPassable() && !isOre(checkLocation.getBlock())) {
                return false;
            }
        }
        return true;
    }

    //    private void BreakTheBlock(Block blockWeWantToBreak) {
//        double radius = 4;
//        Log("Breaking Block with Block Breaker");
//        BlockBreaker.BlockBreakerConfiguration cfg = new BlockBreaker.BlockBreakerConfiguration();
//
//        if (radius == -1) {
//            cfg.radius(radius);
//        } else {
//            cfg.radius(4);
//        }
//
//        Log("Breaking Block");
//        Log(blockWeWantToBreak.getLocation().toString());
//        Log(blockWeWantToBreak.getType().toString());
//
//        if (blockWeWantToBreak.getType() != Material.AIR) {
//            BlockBreaker breaker = npc.getBlockBreaker(blockWeWantToBreak, cfg);
//            npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);
//
//            blockBreakerTimeCounter = 0;
//        }
//    }
    private boolean isInventoryFull() {

        for (ItemStack item : npc.getOrAddTrait(Inventory.class).getContents()) {
            if (item == null) {
                //return false because if we found a null item stack then we still have room, and inventory is not full.
                return false;
            }
        }

        return true;

    }

//    private void depositInventory() {
//        Location dropLocation = owner.getLocation().add(1, 0, 1);
//        dropLocation.getBlock().setType(Material.CHEST);
//        Chest chest = (Chest) dropLocation.getBlock().getState();
//
//        for (ItemStack item : npc.getOrAddTrait(Inventory.class).getContents()) {
//            if (item != null) {
//                chest.getInventory().addItem(item);
//
//            }
//        }
//
//        npc.getOrAddTrait(Inventory.class).getContents();
//
//        for (ItemStack item : npc.getOrAddTrait(Inventory.class).getContents()) {
//            if (item != null) {
//               item = null;
//
//            }
//        }
//
//
//    }

//    private void depositInventory() {
//        Location dropLocation = owner.getLocation().add(1, 0, 1);
//        Location dropLocation2 = owner.getLocation().add(2, 0, 1);
//        dropLocation.getBlock().setType(Material.CHEST);
//        dropLocation2.getBlock().setType(Material.CHEST);
//        Chest chest1 = (Chest) dropLocation.getBlock().getState();
//        Chest chest2 = (Chest) dropLocation2.getBlock().getState();
//
//        Inventory npcInventory = npc.getOrAddTrait(Inventory.class);
//
//        int chest1Counter = 0;
//
//
//        for (ItemStack item : npcInventory.getContents()) {
//            if (item != null && item.getType() != Material.AIR) {
//                if(chest1Counter <= 26){
//                    chest1Counter++;
//                    chest1.getInventory().addItem(item);
//                }
//                else{
//                    chest2.getInventory().addItem(item);
//                }
//
//            }
//        }
//
//        // Clear the NPC inventory properly
//        for (int i = 0; i < npcInventory.getInventoryView().getSize(); i++) {
//
//            npcInventory.getInventoryView().setItem(i,null);
//        }
//
//        npc.removeTrait(Inventory.class);
//    }

//    private void depositInventory() {
//        Location dropLocation = owner.getLocation().add(1, 0, 1);
//        Location adjacentLocation = dropLocation.clone().add(1, 0, 0); // Place second chest next to the first one
//
//        dropLocation.getBlock().setType(Material.CHEST);
//        adjacentLocation.getBlock().setType(Material.CHEST);
//
//        // Get the chest state to store items
//        Chest chest = (Chest) dropLocation.getBlock().getState();
//        Chest adjacentChest = (Chest) adjacentLocation.getBlock().getState();
//
//
//
//        // Transfer inventory items
//        org.bukkit.inventory.Inventory chestInventory = chest.getInventory();
//        org.bukkit.inventory.Inventory adjacentChestInventory = adjacentChest.getInventory();
//
//        Inventory npcInventory = npc.getOrAddTrait(Inventory.class);
//
//        for (ItemStack item : npcInventory.getContents()) {
//            if (item != null) {
//                // Try adding to the first chest, if full then add to the second chest
//                HashMap<Integer, ItemStack> leftover = chestInventory.addItem(item);
//                if (!leftover.isEmpty()) {
//                    adjacentChestInventory.addItem(leftover.values().toArray(new ItemStack[0]));
//                }
//            }
//        }
//
//        // Clear NPC inventory
//        for (int i = 0; i < npcInventory.getInventoryView().getSize(); i++) {
//            npcInventory.setItem(i, null);
//        }
//
//        npc.removeTrait(Inventory.class);
//    }


    private void depositInventory() {
        Location dropLocation = owner.getLocation().add(1, 0, 1);
        Location adjacentLocation = dropLocation.clone().add(1, 0, 0); // Adjacent block for double chest

        // Place chests
        dropLocation.getBlock().setType(Material.CHEST);
        adjacentLocation.getBlock().setType(Material.CHEST);

        // Get the Chest state after placing
        Chest chest1 = (Chest) dropLocation.getBlock().getState();
        Chest chest2 = (Chest) adjacentLocation.getBlock().getState();

        // Make sure they merge into a double chest
        org.bukkit.block.data.type.Chest chestData1 = (org.bukkit.block.data.type.Chest) chest1.getBlockData();
        org.bukkit.block.data.type.Chest chestData2 = (org.bukkit.block.data.type.Chest) chest2.getBlockData();

        chestData1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
        chestData2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);

        chest1.setBlockData(chestData1);
        chest2.setBlockData(chestData2);
        chest1.update();
        chest2.update();

        // Get the double chest inventory
        org.bukkit.inventory.Inventory chestInventory = ((Chest) dropLocation.getBlock().getState()).getInventory();

        Inventory npcInventory = npc.getOrAddTrait(Inventory.class);

        // Move NPC inventory items into chest
        for (ItemStack item : npcInventory.getContents()) {
            if (item != null) {
                chestInventory.addItem(item);
            }
        }


        // Clear NPC inventory
        for (int i = 0; i < npcInventory.getInventoryView().getSize(); i++) {
            npcInventory.setItem(i, null);
        }

        npc.removeTrait(Inventory.class);
    }


    private void BreakTheBlock(Block blockWeWantToBreak) {
        double radius = 2;
        BlockBreaker.BlockBreakerConfiguration cfg = new BlockBreaker.BlockBreakerConfiguration();

        if (radius == -1) {
            cfg.radius(radius);
        } else {
            cfg.radius(3);
        }

        if (blockWeWantToBreak.getType() != Material.AIR) {
            Log("Breaking Block");
            Log(blockWeWantToBreak.getLocation().toString());
            Log(blockWeWantToBreak.getType().toString());

            BlockBreaker breaker = npc.getBlockBreaker(blockWeWantToBreak, cfg);
            npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);
            blockBreakerTimeCounter = 0;
            new BukkitRunnable() {
                @Override
                public void run() {
                    collectDroppedItems(blockWeWantToBreak.getLocation());
                }
            }.runTaskLater(DevCraftPlugin.getInstance(), 20L);
        }
    }

    private void collectDroppedItems(Location location) {
        World world = location.getWorld();
        List<Item> droppedItems = world.getEntitiesByClass(Item.class).stream()
                .filter(item -> item.getLocation().distanceSquared(location) < 3)
                .collect(Collectors.toList());

        for (Item item : droppedItems) {
            item.setPickupDelay(0);
            item.teleport(npc.getEntity().getLocation());
            //npc.getEntity().getWorld().playSound(npc.getEntity().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            var inv = npc.getOrAddTrait(Inventory.class);
            inv.getInventoryView().addItem(item.getItemStack());
            // item.remove();
        }
    }

    private boolean isOre(Block block) {
        return block.getType().toString().endsWith("_ORE");
    }
}