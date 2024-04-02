package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.trait.trait.Equipment;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Enumeration;

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

        private TreeLoggingState treeLoggingState = TreeLoggingState.StageOne;

        @Override
        public void run() {
            if (!(npc.getEntity() instanceof Player)) return;
            if (!npc.isSpawned()) return;
            if(npc.getDefaultGoalController().isExecutingGoal()) return;
            if (npc.getNavigator().isNavigating()) return;
           // if(npc.getDefaultGoalController().isExecutingGoal()) return;
            if (!CitizensAPI.getNPCRegistry().isNPC(npc.getEntity())) return;



            if(treeLoggingState == TreeLoggingState.StageOne)
            {
                //Search For a Nearby Tree

                //OutputTheLocation of the southside of the tree trunk

            }
            else if(treeLoggingState == TreeLoggingState.StageTwo)
            {
                //Build Ladders and climb to the top of the tree.
            }
            else if(treeLoggingState == TreeLoggingState.StageThree)
            {
                //Chop down each layer of the tree
                //Then walk around and gather items.
            }
            else if(treeLoggingState == TreeLoggingState.StageFour)
            {
                //Remove the trait
            }


            //We want to search around the npc for wood of any kind

            //we want to find

            if(!isFindingOakLog){
                Log("Starting To find Oak trees");
                isFindingOakLog = true;
                Block block;

                //GoBreakBlock(block);

            }


//
//            if(isFindingOakLog)
//            {
//                return;
//            } else{
//                FindNearestOakLogAndGetIt() ;
//            }

        }

    private void GoBreakBlock(Block block) {
        blockBreakerConfiguration.radius(5);
        BlockBreaker breaker = npc.getBlockBreaker(block, blockBreakerConfiguration);
        npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);
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
                    mineBlock(oakLog,surfaceNextToBlock);
                }
                else
                {
                    Bukkit.broadcastMessage("We cannot reach location: " + locationToNavigateTo.toString() );
                }


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
    boolean hitBedrock = false;
    BlockBreaker.BlockBreakerConfiguration blockBreakerConfiguration = new BlockBreaker.BlockBreakerConfiguration();
    private void mineBlock(Block block,Block surfaceNextToBlock) {

        if(surfaceNextToBlock.getLocation().distance(npc.getEntity().getLocation()) > 9)
        {
            npc.getNavigator().setTarget(surfaceNextToBlock.getLocation());
            return;
        }

        blockBreakerConfiguration.radius(10);
        BlockBreaker breaker = npc.getBlockBreaker(block, blockBreakerConfiguration);


        npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);


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
            npc.data().setPersistent(NPC.Metadata.PICKUP_ITEMS,true);
            var eq = npc.getOrAddTrait(Equipment.class);
            eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_AXE));

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



