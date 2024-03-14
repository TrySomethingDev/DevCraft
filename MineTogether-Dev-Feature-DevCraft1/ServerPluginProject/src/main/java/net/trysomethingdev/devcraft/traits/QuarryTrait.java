package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("quarry")
    public class QuarryTrait extends Trait {


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

        int maxSize = 10;
    public QuarryTrait(int length, int width, int depth) {
        super("quarry");

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



            if (!npc.isSpawned() //|| delay-- > 0
            //
             ) {
                return;
            }

            if(miningLocation == null)
            {
                miningLocation = Bukkit.getPlayer("TrySomethingDev").getLocation().clone();

            }


            if (
                    //inventory.firstEmpty() == -1 ||
             hitBedrock || currentDepth >= depth) { // Check if the inventory is full
                returnToSurface();
                npc.removeTrait(QuarryTrait.class);
            } else {
                mineBlock();
            }

            //delay = 20;


        }

        boolean hitBedrock = false;
    private void mineBlock() {
        // Mine a 4x4 area
        new DelayedTask(() -> {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    Block block = miningLocation.clone().add(x, -currentDepth, z).getBlock();
                    if (block.getType().isSolid()) {
                        if(block.getType() == Material.BEDROCK)
                        {
                            hitBedrock = true;
                            return;
                        }
                        inventory.addItem(new ItemStack(block.getType()));
                        block.setType(Material.AIR);
                    }
                }
            }
            currentDepth++;

        }, 5 * 1);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                Block block = miningLocation.clone().add(x, -currentDepth, z).getBlock();
                if (block.getType().isSolid()) {
                    if(block.getType() == Material.BEDROCK)
                    {
                        hitBedrock = true;
                        return;
                    }
                    inventory.addItem(new ItemStack(block.getType()));
                    block.setType(Material.AIR);
                }
            }
        }
        currentDepth++;
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



