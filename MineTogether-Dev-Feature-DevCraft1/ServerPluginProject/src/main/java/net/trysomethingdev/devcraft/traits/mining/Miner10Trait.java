package net.trysomethingdev.devcraft.traits.mining;

import io.papermc.paper.entity.LookAnchor;
import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;


public class Miner10Trait extends Trait {
    private Player owner;
    private boolean isMining = false;
    private static final int SEARCH_RADIUS = 4;
    private int delayCounter;
    private int stuckExecuting;
    private int blockBreakerTimeCounter;

    private static final int INVENTORY_LIMIT = 36;
    public Miner10Trait() {
        super("Miner10Trait");
    }

    public void Log(String message) {
        DevCraftPlugin.getInstance().getLogger().info(message);
    }

    @Override
    public void onAttach() {

        //  Bukkit.broadcastMessage("OnAttach");


        this.isRunImplemented();
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

    private boolean isInventoryFull() {

        for (ItemStack item : npc.getOrAddTrait(Inventory.class).getContents()) {
            if (item == null) {
                //return false because if we found a null item stack then we still have room, and inventory is not full.
                return false;
            }
        }

        return true;

    }

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