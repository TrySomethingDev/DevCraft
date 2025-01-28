package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.util.Pose;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("straightminingtrait")
public class StraightMiningTrait extends Trait {

    public StraightMiningTrait() {
        super("straightminingtrait");
    }

    DevCraftPlugin plugin = null;
    boolean SomeSetting = false;

    // see the 'Persistence API' section
    @Persist("mysettingname")
    boolean automaticallyPersistedSetting = false;

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
            getLogger().info("NPC CLICKED ON - Straight Mining Trait");
            RunMiner();

        }
    }

    private void RunMiner() {

        getServer().getScheduler().runTaskLater(DevCraftPlugin.getInstance(), this::animateNPC, 20L); // Delay 1 second
    }

    private int tickCounter = 1;

    private int rotation = 0;


    @Override
    public void run() {

        if (!npc.isSpawned()) return;

        if (npc.getNavigator().isNavigating()) {
            return;
        }

//            rotation = (rotation + 10) % 360;
//            npc.faceLocation(npc.getEntity().getLocation().add(Math.cos(Math.toRadians(rotation)), 0, Math.sin(Math.toRadians(rotation))));


    }
    int zToMine = 1;
    int headHeightY = -1;
    int footHeightY = -1;

    int xToMine = -1;

    int stepsForwardWithoutBreakingBlock = 0;
    public void animateNPC() {
        // Get the NPC by ID or name
        NPC npc = this.npc; // Replace 1 with your NPC ID
//        if(xToMine = -1)
//        {
//            //initialize our location values
//            xToMine = npc.getEntity().getLocation().getX();
//            zToMine = npc.getEntity().getLocation().getZ();
//
//        }


        if (npc == null) {
            getLogger().warning("NPC not found!");
            return;
        }

        // Sequence of animations using BukkitRunnable
        new BukkitRunnable() {
            int step = 0;
            Block blockToMineHeadHeight;
            Block blockToMineFootHeight;
            BlockBreaker.BlockBreakerConfiguration blockBreakerConfiguration = new BlockBreaker.BlockBreakerConfiguration();



            @Override
            public void run() {

              //  Bukkit.broadcastMessage("Bukkit Runnable is running");

                if (!npc.isSpawned()) {
                    cancel();
                    return;
                }

                switch (step) {
                    case 0: // Step 1: Get Next Block

                        footHeightY = (int)npc.getEntity().getY();
                        headHeightY = footHeightY + 1;

                        xToMine = (int)npc.getEntity().getLocation().toCenterLocation().getX();
                        zToMine = (int)npc.getEntity().getZ() + 1;
                        //is there a blcok at 65
                        blockToMineHeadHeight = npc.getEntity().getWorld().getBlockAt(xToMine, headHeightY, zToMine);
                        Bukkit.broadcastMessage("Block to Mine Head Height: " + blockToMineHeadHeight );
                        //is there a block at 64
                        blockToMineFootHeight = npc.getEntity().getWorld().getBlockAt(xToMine, footHeightY, zToMine);

                        //if Not move forward
                        if(blockToMineFootHeight.getType().isAir() && blockToMineHeadHeight.getType().isAir()) {

                           // If we have reached this twice, and we have not broken a block then end.
                            if(stepsForwardWithoutBreakingBlock > 1)
                            {
                                cancel();
                                break;
                            }
                            Location targetLocation = npc.getEntity().getLocation().add(0, 0, 1); // Move 1 blocks forward
                            Bukkit.broadcastMessage(targetLocation.toString());
                            npc.getNavigator().setTarget(targetLocation);
                            stepsForwardWithoutBreakingBlock++;
                            zToMine++;
                            RunMiner();
                            cancel(); // End the sequence
                            break;
                        }

                        break;

                    case 1: // Step 2: Break first block
                        Bukkit.broadcastMessage("Step 1");
                        blockBreakerConfiguration.radius(3);
                        BlockBreaker breaker = npc.getBlockBreaker(blockToMineHeadHeight, blockBreakerConfiguration);
                        npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 1);
                        break;
                    case 2: // Step 3: Break Foot Block
                        Bukkit.broadcastMessage("Step 2");
                        blockBreakerConfiguration.radius(3);
                        BlockBreaker breaker1 = npc.getBlockBreaker(blockToMineFootHeight, blockBreakerConfiguration);
                        npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker1), 1);
                        stepsForwardWithoutBreakingBlock = 0;
                        break;
                    case 3: // Step 4:
                        Bukkit.broadcastMessage("Step 3");
                        RunMiner();
                        cancel(); // End the sequence
                        break;
                    default:
                        Bukkit.broadcastMessage("Step Default");
                        cancel(); // End the sequence
                        return;
                }
                step++;
            }
        }.runTaskTimer(DevCraftPlugin.getInstance(), 0L, 20L); // 20 ticks (1 second) between each step
    }


    private static void Log(String s) {
        getLogger().info(s);
    }

    private void NPCJump() {
        new DelayedTask(() -> {
            npc.getEntity().setVelocity(new Vector(0, 1f, 0));

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

    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

}



