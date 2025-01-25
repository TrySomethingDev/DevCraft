package net.trysomethingdev.devcraft.traits;

//

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.LookClose;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("sortingtrait")
public class SortingTrait extends Trait {


    private final Map<Location, Location> itemFrameToChest = new HashMap<>();
    private Plugin pluggin;
    private int _currentPhase = 1;
    private boolean _phase1Triggered = false;
    private boolean _phase2Triggered = false;
    private boolean _phase3Triggered = false;
    private boolean _phase4Triggered = false;
    private ItemStack _stackToSort;
    private Location _locationOfTargetChestToPlaceItemIn
            ;
    private boolean _phase5Triggered;

    public SortingTrait() {
        super("sortingtrait");
    }

    boolean SomeSetting = false;

    // see the 'Persistence API' section
    @Persist("mysettingname")
    boolean automaticallyPersistedSetting = false;

    public SortingTrait(DevCraftPlugin plugin) {
        super("sortingtrait");
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
        key.setBoolean("SomeSetting", SomeSetting);
    }

    // An example event handler. All traits will be registered automatically as Spigot event Listeners
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (event.getNPC() == this.getNPC()) {
            Bukkit.getLogger().info("NPC CLICKED ON - Sorting Trait");
        }
    }

    private boolean hasRun = false;

    private Location _unsortedChestLocation;
    @Override
    public void run() {

        if (!npc.isSpawned()) return;
        if (npc.getNavigator().isNavigating()) {
            Bukkit.getLogger().info("Is Navigating");
            return;
        }

        if (_currentPhase == 1 && !_phase1Triggered) {

            _phase1Triggered = true;
            _unsortedChestLocation = new Location(npc.getEntity().getWorld(), 3, -60, 13);

            Bukkit.getLogger().info("Phase 1 Triggered");
            //Phase 1 - Walk to Unsorted Chest
            new DelayedTask(() -> {
                // Add traits to NPC
                npc.getOrAddTrait(LookClose.class).lookClose(true);
                npc.getOrAddTrait(Inventory.class);
                FindAndGoToUnsortedChest();

            }, 20 * 1);

        } else if (_currentPhase == 2 && !_phase2Triggered) {

            _phase2Triggered = true;
            Bukkit.getLogger().info("Phase 2 Triggered");
            //Take Item From Chest

            new DelayedTask(() -> {
                // Add traits to NPC
                GetItemsFromUnsortedChest(_unsortedChestLocation);
            }, 20 * 1);

        } else if (_currentPhase == 3 && !_phase3Triggered) {

            _phase3Triggered = true;
            Bukkit.getLogger().info("Phase 3 Triggered");
            //Walk to Target Chest

            new DelayedTask(() -> {

                FindAndGoToTargetChest();
            }, 20 * 1);
        }
        else if (_currentPhase == 4 && !_phase4Triggered){

            _phase4Triggered = true;
            Bukkit.getLogger().info("Phase 4 Triggered");

            new DelayedTask(() -> {

                Bukkit.getLogger().info(_locationOfTargetChestToPlaceItemIn.toString());
                PlaceItemInChest(_stackToSort,_locationOfTargetChestToPlaceItemIn);

            }, 20 * 1);
        }

        else if (_currentPhase == 5 && !_phase5Triggered){
            _phase5Triggered = true;
            Bukkit.getLogger().info("Phase 5 Triggered");

            npc.removeTrait(this.getClass());
            npc.addTrait(this.getClass());

        }







        //Find Chest Logic goes here.


    }

    private void FindAndGoToUnsortedChest() {

        _unsortedChestLocation = findInboxNearNPC(npc.getEntity().getLocation(),30);

        GoToUnsortedChest(_unsortedChestLocation);

    }

    public Location findInboxNearNPC(Location searchStartLocation, int radius) {
        Location npcLocation = searchStartLocation;
        int startX = npcLocation.getBlockX() - radius;
        int startY = npcLocation.getBlockY() - radius;
        int startZ = npcLocation.getBlockZ() - radius;

        int endX = npcLocation.getBlockX() + radius;
        int endY = npcLocation.getBlockY() + radius;
        int endZ = npcLocation.getBlockZ() + radius;

        // Loop through blocks in the defined cube around the NPC
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = npcLocation.getWorld().getBlockAt(x, y, z);

                    // Check if the block is a sign
                    if (block.getState() instanceof Sign) {
                        Sign sign = (Sign) block.getState();

                        // Check if the sign contains the word "INBOX"
                        for (String line : sign.getLines()) {
                            if (line.equalsIgnoreCase("INBOX")) {
                                // Get the attached chest
                                Location chestLocation = getAttachedChest(sign);
                                if (chestLocation != null) {
                                    return chestLocation; // Return the first chest found
                                }
                            }
                        }
                    }
                }
            }
        }
        return null; // No "INBOX" sign with an attached chest found in the area
    }

    private void PlaceItemInChest(ItemStack stackToSort, Location locationOfTargetChestToPlaceItemIn) {
        Bukkit.getLogger().info("Placing Item IN Chest");
        Chest sortedChest = getChestAt(locationOfTargetChestToPlaceItemIn);
        if (sortedChest == null || isChestEmpty(sortedChest)) {
            Bukkit.getLogger().info("sorted Chest is null or empty");
        }
        addItemToChest(sortedChest,stackToSort);
        _currentPhase = 5;
    }

    private void FindAndGoToTargetChest() {
        _locationOfTargetChestToPlaceItemIn = findTargetChestWithItemFrame(_stackToSort,_unsortedChestLocation);
        Bukkit.getLogger().info(_locationOfTargetChestToPlaceItemIn.toString());
        npc.getNavigator().setTarget(_locationOfTargetChestToPlaceItemIn);
        _currentPhase = 4;
    }


    private void GoToUnsortedChest(Location unsortedChestLocation) {
        new DelayedTask(() -> {

            Chest unsortedChest = getChestAt(unsortedChestLocation);
            if (unsortedChest == null || isChestEmpty(unsortedChest)) {
                RemoveTrait();
                DanceForSeconds(4);
                return; // No chest or chest is empty
            }

            npc.getNavigator().setTarget(unsortedChestLocation);
            _currentPhase = 2;

        }, 20 * 1);// Run every second
    }

    private void DanceForSeconds(int i) {
        new DelayedTask(() -> {
            npc.addTrait(DanceTrait.class);
        }, 20 * 2);

        new DelayedTask(() -> {
            npc.removeTrait(DanceTrait.class);
        }, 20 * (i + 1));
    }

    private void RemoveTrait() {
        new DelayedTask(() -> {
            this.npc.removeTrait(this.getClass());
            Bukkit.getLogger().info("Removing Sorting Trait");
        }, 20 * 1);// Run every

    }


    private void GetItemsFromUnsortedChest(Location unsortedChestLocation) {

        new DelayedTask(() -> {


            Chest unsortedChest = getChestAt(unsortedChestLocation);
            if (unsortedChest == null || isChestEmpty(unsortedChest)) {
                return; // No chest or chest is empty
            }

            _stackToSort = getFirstItemStack(unsortedChest);

            removeItemFromChest(unsortedChest,_stackToSort);

            if (_stackToSort == null) {
                RemoveTrait();
                return;
            }

            _currentPhase = 3;

        }, 20 * 1);// Run every second
    }

    private boolean isChestEmpty(Chest chest) {
        return chest.getBlockInventory().isEmpty();
    }

    private ItemStack getFirstItemStack(Chest chest) {
        for (ItemStack item : chest.getBlockInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return item;
            }
        }
        return null;
    }

    private Location findTargetChestWithItemFrame(ItemStack stack, Location center) {
        for (Entity entity : center.getWorld().getNearbyEntities(center, 10, 10, 10)) {
            if (entity instanceof ItemFrame itemFrame) {
                ItemStack item = itemFrame.getItem();
                if (item != null && item.isSimilar(stack)) {
                    Block attachedBlock = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace());
                    if (attachedBlock.getType() == Material.CHEST) {
                        return attachedBlock.getLocation();
                    }
                }
            }
        }
        return null;
    }


    public Location getAttachedChest(Sign sign) {
        // Ensure the sign is a wall sign
        if (sign.getBlock().getBlockData() instanceof WallSign) {
            WallSign wallSign = (WallSign) sign.getBlock().getBlockData();
            Block attachedBlock = sign.getBlock().getRelative(wallSign.getFacing().getOppositeFace());

            // Check if the attached block is a chest
            if (attachedBlock.getType() == Material.CHEST || attachedBlock.getType() == Material.TRAPPED_CHEST) {
                return attachedBlock.getLocation();
            }
        }
        return null; // No chest attached
    }

    private boolean removeItemFromChest(Chest chest, ItemStack stack) {
        return chest.getBlockInventory().removeItem(stack).isEmpty();
    }

    private boolean addItemToChest(Chest chest, ItemStack stack) {
        Bukkit.getLogger().info("Adding Item to Chest");
        return chest.getBlockInventory().addItem(stack).isEmpty();
    }

    private Chest getChestAt(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.CHEST) {
            return (Chest) block.getState();
        }
        return null;
    }

    private static void Log(String s) {
        Bukkit.getLogger().info(s);
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



