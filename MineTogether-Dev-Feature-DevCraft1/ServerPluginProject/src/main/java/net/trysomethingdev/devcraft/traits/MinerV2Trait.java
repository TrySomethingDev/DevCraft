package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.Settings;
import net.citizensnpcs.api.ai.tree.StatusMapper;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.HologramTrait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.NpcMiningHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("minerv2trait")
public class MinerV2Trait extends Trait {
    private int timeElapsed;
    private Location locationOfSign;
    private int stuckExecuting;
    private Block blocktoBreak;
    private double currentMiningY;
    private Location firstBlockToMineBehindTheSign;

    public MinerV2Trait() {
        super("minerv2trait");
        plugin = JavaPlugin.getPlugin(DevCraftPlugin.class);
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
            Bukkit.getLogger().info("NPC CLICKED ON");
            NPCJump();
        }
    }

    Location mineEntrance = null;

    @Override
    public void run() {

        if (!npc.isSpawned()) return;

        if (npc.getNavigator().isNavigating()) {
            return;
        }

        if (npc.getDefaultGoalController().isExecutingGoal()) {
            ///     Bukkit.broadcastMessage("ExecutingGoal");
            stuckExecuting++;
            if (stuckExecuting > 100) {
                //          Bukkit.broadcastMessage("Stuck Executing");
                Bukkit.broadcastMessage(blocktoBreak.toString());

            }
            return;
        } else {
            stuckExecuting = 0;
        }

        if (timeElapsed < 20) {
            timeElapsed++;
            return;
        }
        timeElapsed = 0;

        if (mineEntrance == null) {
            npc.getOrAddTrait(FollowCustomTrait.class);
            locationOfSign = findInboxNearNPC(npc.getEntity().getLocation(), 5);
        } else npc.removeTrait(FollowCustomTrait.class);

        //Try to find a sign with Mine on it.

        if (locationOfSign != null && mineEntrance == null) {
            mineEntrance = locationOfSign;
            npc.removeTrait(FollowCustomTrait.class);
            var holo = npc.getOrAddTrait(HologramTrait.class);
            holo.addTemporaryLine("I found the sign, I will mine there", 80);
            npc.getNavigator().setTarget(locationOfSign);
            firstBlockToMineBehindTheSign = mineEntrance.clone().add(new Location(npc.getEntity().getWorld(),0,-1,1));
            currentMiningY = firstBlockToMineBehindTheSign.getY();
        }


        if (mineEntrance == null) return;



        //Miner Logic
        var minerHelper = new NpcMiningHelper(npc);
       // minerHelper.faceNorth();

        //We now know that npc getLocation returns the location of the NPC's Feet.


        //Mine down to Y=40
        if(npc.getEntity().getLocation().getY() > 40)
        {
            //Make sure where the NPC Feet are that there is not another block there on same level.

        }

         plugin.getLogger().info(firstBlockToMineBehindTheSign.toString());
       // blocktoBreak = getNextBlockToMine(firstBlockToMineBehindTheSign);
        BreakTheBlock(firstBlockToMineBehindTheSign.getBlock());


    }

    public static Block getNextBlockToMine(Location npcLocation) {
        World world = npcLocation.getWorld();
        int x = npcLocation.getBlockX();
        int y = npcLocation.getBlockY();
        int z = npcLocation.getBlockZ();

        // If above Y = -50, mine diagonally downward
        if (y > -50) {
            return world.getBlockAt(x + 1, y - 1, z); // Move forward and down
        }

        // If at Y = -50, mine horizontally at foot and head height
        Block footBlock = world.getBlockAt(x + 1, y, z);  // Foot level block
        Block headBlock = world.getBlockAt(x + 1, y + 1, z); // Head level block

        // Prioritize foot block, then head block
        if (!footBlock.getType().isAir()) {
            return footBlock;
        } else {
            return headBlock;
        }
    }

//    private void mineVein(Block block) {
//        for (BlockFace face : BlockFace.values()) { // Checks all six adjacent directions
//            Block adjacent = block.getRelative(face);
//            if (targetBlocks.contains(adjacent.getType())) { // If the block is a target ore
//                mineBlock(adjacent);
//                mineVein(adjacent); // Recursively mine the next block in the vein
//            }
//        }
//    }
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
                            if (line.equalsIgnoreCase("MINE")) {
                                // Get the attached chest
                                return block.getLocation();

                            }
                        }
                    }
                }
            }
        }

        return null; // No "INBOX" sign with an attached chest found in the area
    }

    public double getCenteredCoordinate(double coord) {
        return Math.floor(coord) + 0.5;
    }

    public void centerPlayerOnBlock() {
        Location location = npc.getEntity().getLocation();
        double x = getCenteredCoordinate(location.getX());
        double z = getCenteredCoordinate(location.getZ());
        location.setX(x);
        location.setZ(z);
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public Vector getDirectionVector(Location location) {
        double pitch = ((location.getPitch() + 90) * Math.PI) / 180;
        double yaw = ((location.getYaw() + 90) * Math.PI) / 180;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        return new Vector(x, z, y);
    }

    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }

    public void drawLine(Location point1, Location point2) {
        World world = point1.getWorld();
        double distance = point1.distance(point2);

        for (double d = 0; d <= 1; d += 0.5 / distance) {
            double x = point1.getX() + (point2.getX() - point1.getX()) * d;
            double y = point1.getY() + (point2.getY() - point1.getY()) * d;
            double z = point1.getZ() + (point2.getZ() - point1.getZ()) * d;

            world.spawnParticle(Particle.HEART, new Location(world, x, y, z), 1, 0, 0, 0);
        }
    }

    private void NPCJump() {
        npc.getEntity().setVelocity(new Vector(0, 1f, 0));
    }

    private void BreakTheBlock(Block blockWeWantToBreak) {
        double radius = 10;

        BlockBreaker.BlockBreakerConfiguration cfg = new BlockBreaker.BlockBreakerConfiguration();
        if (radius == -1) {
            cfg.radius(radius);
        } else if (Settings.Setting.DEFAULT_BLOCK_BREAKER_RADIUS.asDouble() > 0) {
            cfg.radius(Settings.Setting.DEFAULT_BLOCK_BREAKER_RADIUS.asDouble());
        }



        if (blockWeWantToBreak.getType() != Material.AIR) {
            // Create a new BlockBreaker for the NPC
            BlockBreaker breaker = npc.getBlockBreaker(blockWeWantToBreak, cfg);
            npc.getDefaultGoalController().addBehavior(StatusMapper.singleUse(breaker), 10);

        }

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



