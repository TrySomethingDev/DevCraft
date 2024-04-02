package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.Settings;
import net.citizensnpcs.api.ai.tree.Sequence;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Queue;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("quarry")
    public class QuarryTrait extends Trait {


    private boolean readyForNextBlock;
    private Queue<Block> queueOfBlocks;
    private boolean finishedQueue;
    private Block block;
    private boolean scanning;
    private boolean arrivedAtMiningLocation;

    public QuarryTrait() {
        super("quarry");
        inventory = Bukkit.createInventory(null, 27); // Create a new inventory for the NPC
    }

        DevCraftPlugin plugin = null;

        boolean SomeSetting = false;

        // see the 'Persistence API' section
        @Persist("mysettingname") boolean automaticallyPersistedSetting = false;

        int length = 1;
        int width = 1;
        int depth = 1;

        int currentDepth = 0;

        int minSize = 4;
        int maxSize = 80;
    public QuarryTrait(int length, int width, int depth, DevCraftPlugin plugin) {
        super("quarry");

        this.plugin = plugin;

        if(length < minSize) length = minSize;
        if(width < minSize) width = minSize;
        if(depth < minSize) depth = minSize;

        if(length > maxSize) length = maxSize;
        if(width > maxSize) width = maxSize;
        if(depth > 400) depth = 400;

        this.length = length;
        this.width = width;
        this.depth = depth;
        inventory = Bukkit.createInventory(null, 27); // Create a new inventory for the NPC


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
               Bukkit.getLogger().info("NPC CLICKED ON - Quarry");
                NPCJump();
            }
        }

        private int tickCounter = 1;

        DevCraftTwitchUser user;
        private int delay;

    private Location miningLocation;

    private Inventory inventory;
        @Override
        public void run() {
            
            if (!npc.isSpawned())  return;
            if (npc.getNavigator().isNavigating()) return;
            if(npc.getDefaultGoalController().isExecutingGoal()) return;

            //We need to arrive at the mining location
            if(!arrivedAtMiningLocation)
            {
                if(npc.getEntity().getLocation().distance(plugin.getMiningLocationStartPoint()) > 2)
                {
                    npc.getNavigator().setTarget(plugin.getMiningLocationStartPoint());
                }
                else{
                    arrivedAtMiningLocation = true;
                }
            }


            if(miningLocation == null)
            {
                miningLocation = plugin.getMiningLocationStartPoint();
                CreateListOfLocationsToMine(miningLocation);
                readyForNextBlock = true;
            }




            if(block !=null && block.getType().isSolid())
            {
                block.breakNaturally();
                return;

            } else if(block != null && !block.getType().isSolid())
            {
                readyForNextBlock = true;
            }


            //If inventory is full or we reach our desired depth, or bedrock, then TP up to the surface and find the player.
             if (
                    inventory.firstEmpty() == -1 ||
                    finishedQueue || hitBedrock || currentDepth >= depth) { // Check if the inventory is full
               // returnToSurface();
                npc.removeTrait(QuarryTrait.class);
                npc.teleport(plugin.getNpcGlobalSpawnPoint(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                npc.getOrAddTrait(FollowTraitCustom.class);
            } else if (readyForNextBlock){

                 block = queueOfBlocks.poll();
                 if(block == null){
                  finishedQueue = true;
                 }
                 else{

                     mineBlock(block);

                 }
            }
        }

    private void CreateListOfLocationsToMine(Location miningLocation) {
        queueOfBlocks = new LinkedList<>();
        for(int y = 0; y < depth; y++  )
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    Block block = miningLocation.clone().add(x, -y, z).getBlock();
                    if (block.getType().isSolid()) {
                         if(block.getType() == Material.BEDROCK)
                        {
                        //Done with List
                        return;
                         }
                        else{
                             queueOfBlocks.add(block);
                         }
                }
            }
        }
       // currentDepth++;
    }



    boolean hitBedrock = false;
        BlockBreaker.BlockBreakerConfiguration blockBreakerConfiguration = new BlockBreaker.BlockBreakerConfiguration();
    private void mineBlock(Block block) {
        // Mine a 4x4 area

//        new DelayedTask(() -> {
//            npc.getDefaultGoalController().isExecutingGoal()
//
//        }, 20 * 1);
                if(block.getLocation().distance(npc.getEntity().getLocation()) > 2)
                {
                    npc.getNavigator().setTarget(block.getLocation());
                    return;
                }

                blockBreakerConfiguration.radius(10);
                BlockBreaker breaker = npc.getBlockBreaker(block, blockBreakerConfiguration);


                npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);


    }






    private void BreakTheBlock(Block blockWeWantToBreak) {
//        inventory.addItem(new ItemStack(blockWeWantToBreak.getType()));
//        block.setType(Material.AIR);

        double radius = 10;

        BlockBreaker.BlockBreakerConfiguration cfg = new BlockBreaker.BlockBreakerConfiguration();
        if (radius == -1) {
            cfg.radius(radius);
        } else if (Settings.Setting.DEFAULT_BLOCK_BREAKER_RADIUS.asDouble() > 0) {
            cfg.radius(Settings.Setting.DEFAULT_BLOCK_BREAKER_RADIUS.asDouble());
        }


//        if (npc.getEntity() instanceof InventoryHolder) {
//            cfg.blockBreaker((block, itemstack) -> {
//                org.bukkit.inventory.Inventory inventory = ((InventoryHolder) npc.getEntity()).getInventory();
//                Location location = npc.getEntity().getLocation();
//                for (ItemStack drop : block.getDrops(itemstack)) {
//                    for (ItemStack unadded : inventory.addItem(drop).values()) {
//                        location.getWorld().dropItemNaturally(npc.getEntity().getLocation(), unadded);
//                    }
//                }
//            });
//        }
//        BlockBreaker breaker = npc.getBlockBreaker(blockWeWantToBreak, cfg);
//        npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);

        if (blockWeWantToBreak.getType() != Material.AIR) {
            // Create a new BlockBreaker for the NPC

            //blockWeWantToBreak.breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE));
            BlockBreaker breaker = npc.getBlockBreaker(blockWeWantToBreak, cfg);
            npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 10);

        }

    }


    private void returnToSurface() {
        // Place ladders and return to the surface
        for (int y = 0; y < depth; y++) {
            Block block = miningLocation.clone().add(0, -y, 0).getBlock();
            block.getRelative(BlockFace.SOUTH).setType(Material.COBBLESTONE);
            block.setType(Material.LADDER);
        }

        // Place a chest and transfer the inventory
        Block chestBlock = miningLocation.clone().add(0, 1, 0).getBlock();
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();
        chest.getInventory().setContents(inventory.getContents());

        // Clear the inventory
        inventory.clear();
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


            npc.removeTrait(FollowTraitCustom.class);
            npc.data().setPersistent(NPC.Metadata.PICKUP_ITEMS,true);

            //   inventory = Bukkit.createInventory(null, 36); // Create a new inventory for the NPC
            var eq = npc.getOrAddTrait(Equipment.class);
            eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_PICKAXE));
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



