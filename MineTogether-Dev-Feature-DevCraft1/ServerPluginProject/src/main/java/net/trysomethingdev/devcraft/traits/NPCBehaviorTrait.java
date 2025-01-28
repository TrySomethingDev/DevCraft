package net.trysomethingdev.devcraft.traits;

import com.denizenscript.denizen.nms.interfaces.FishingHelper;
import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.services.NPCMiningState;
import net.trysomethingdev.devcraft.services.NPCState;
import net.trysomethingdev.devcraft.services.NPCStateManager;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;

public class NPCBehaviorTrait extends Trait {

    private DevCraftPlugin plugin;
    private final NPCStateManager stateManager;
    private Player mainPlayer;
    private boolean stateChanged;

    public NPCBehaviorTrait() {
        super("NPCBehaviorTrait");
        this.plugin = DevCraftPlugin.getInstance(); // Save the plugin instance for future use
        this.stateManager = new NPCStateManager(npc,this);
    }

    @Override
    public void run() {

        if(plugin == null) plugin = DevCraftPlugin.getInstance();
        if(mainPlayer == null) mainPlayer = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName());


        switch (stateManager.getCurrentState()) {
            case FOLLOWING:
                // Continuously execute following behavior
                if(flock == null) OnStartFollow();
                FollowMainPlayer(mainPlayer);
                break;
            case MINING:
                // Continuously execute mining behavior
                MiningFunction();
                break;
            case CHOPPING:
                // Continuously execute chopping behavior
                ChoppingFunction();
                break;
            // Handle other states as needed

            default:
                Bukkit.broadcastMessage("Unhandled State" + stateManager.getCurrentState());
        }
    }

    private void ChoppingFunction() {

      //  Bukkit.broadcastMessage("I am Chopping");
    }

    private void MiningFunction() {
      //  Bukkit.broadcastMessage("I am mining");

        var miningState = NPCMiningState.InitialTravelingToPlayer;
        switch (miningState){
            case InitialTravelingToPlayer -> {
                // Example: Teleport NPC to a mining location
                Location targetLocation = getSafeLocationBehindPlayer(mainPlayer);

                // Ensure this runs on the main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (npc.isSpawned()) {
                        npc.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        plugin.getLogger().info("NPC teleported to mining location.");
                    }
                });
                break;
            }
        }

        new DelayedTask(() -> {
            var foo = 4;

            //TP to Player


        //Throw Item Frame and PickAxe at Player

        //Wait for ItemFrame to be placed

        //Start Mining

        //When Complete Mining TP to player again

        //Place Chest Near Player

        //Fill Chest if Mined Materials

        //When Player Closes Chest take all materials in Chest back to Camp.

        //            //Have NPC Appear Behind wherever Player is...
//
//            var mainPlayerName = plugin.getMainPlayerUserName();
          //  var playerEntity = Bukkit.getPlayer(mainPlayerName);
//
//            var playerLocation = playerEntity.getLocation();
//
//            var playerFacingDirection = playerEntity.getLocation().getDirection();
//
       //     var locationBehindPlayer = getSafeLocationBehindPlayer(playerEntity);
////
////            var combinedString = "We think Dev is facing " + playerFacingDirection;
////            Bukkit.broadcastMessage(combinedString);
//
//
//            if (npc == null) {
//              //  if (npc == null) Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
//                //return;
//
//                npcHelper.getOrCreateNPCAndSpawnIt(user,locationBehindPlayer);
//            }
//            else{
//                npc.teleport(locationBehindPlayer, PlayerTeleportEvent.TeleportCause.PLUGIN);
//            }
//
//
//
//
//           //Try to make seem like they are catching up with you.
//            ;
//
//            //npc4.getOrAddTrait(FollowCustomTrait.class);
//
//
//
//            //NPC Throw down a item frame and a wood pickaxe
//
//
//            npc.getOrAddTrait(ThrowItemTrait.class);
//
//            //Wait for Player to place Item Frame and Pickaxe
//
//            //Find Item Frame and PickAxe
//
//            // Start Mining
//
//            //When finished mining
//
//            //Appear behind player again.
//
//            //Place down chest
//
//            //Put everything mined in chest
//
//            //Wait for player to open chest.
//
//            // When player closes chest.
//
//            //NPC picks up chest and remaining items
//
//            //Take them to main camp and stores them.
//
//           // npc.getOrAddTrait(MinerTrait.class);
    }, 20);

    //  var playerEntity = Bukkit.getPlayer(plugin.getMainPlayerUserName());
    //Have NPC make some Attention Getting sounds

    // PlayAlertSound(playerEntity,Sound.BLOCK_NOTE_BLOCK_BASS,20);
    // PlayAlertSound(playerEntity,Sound.BLOCK_NOTE_BLOCK_BELL,30);



}



    private static void PlayAlertSound(Player playerEntity, Sound sound, int ticksToWait) {

        new DelayedTask(() -> {

            playerEntity.getWorld().playSound(playerEntity.getLocation(), sound, 1.0f, 1.0f);

        }, ticksToWait);

    }

    /**
     * Gets a safe location 20 blocks behind the player with at least 3 blocks of air.
     * If the initial location is not safe, it searches upward block by block until a safe location is found.
     *
     * @param player The player whose location to calculate from.
     * @return A safe location 20 blocks behind the player with at least 3 blocks of air, or null if not found.
     */
    public static Location getSafeLocationBehindPlayer(Player player) {
        // Get the player's current location
        Location playerLocation = player.getLocation();

        // Get the player's direction vector
        Vector direction = playerLocation.getDirection();

        // Normalize the direction vector to make its length 1
        direction = direction.normalize();

        // Multiply the direction vector by -20 to go 20 blocks behind
        Vector offset = direction.multiply(-20);

        // Calculate the location 20 blocks behind
        Location behindLocation = playerLocation.clone().add(offset);

        // Search for a safe location starting from the calculated point
        return findSafeLocation(behindLocation);
    }

    /**
     * Finds a safe location with at least 3 blocks of air by searching upward block by block.
     *
     * @param location The starting location to check.
     * @return A safe location with at least 3 blocks of air, or null if no safe location is found within bounds.
     */
    private static Location findSafeLocation(Location location) {
        Location currentLocation = location.clone();

        // Search upward up to the world height limit
        while (currentLocation.getY() < location.getWorld().getMaxHeight()) {
            if (isSafeLocation(currentLocation)) {
                return currentLocation;
            }
            // Move up one block
            currentLocation.add(0, 1, 0);
        }

        // Return null if no safe location is found
        return null;
    }

    /**
     * Checks if a location has at least 3 blocks of air.
     *
     * @param location The location to check.
     * @return True if the location has at least 3 blocks of air, false otherwise.
     */
    private static boolean isSafeLocation(Location location) {
        Location base = location.clone();
        Location above1 = base.clone().add(0, 1, 0);
        Location above2 = base.clone().add(0, 2, 0);

        // Check if all 3 blocks are air
        return base.getBlock().getType() == Material.AIR &&
                above1.getBlock().getType() == Material.AIR &&
                above2.getBlock().getType() == Material.AIR;
    }
    public void setState(NPCState state) {
        Bukkit.broadcastMessage("Changing State to " + state.name());
        savedState = state;
        stateManager.transitionTo(state,npc);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private Flocker flock;
    private double margin = 5;
    public void OnStartFollow() {
        flock = new Flocker(npc, new RadiusNPCFlock(10), new SeparationBehavior(1000));
    }
    private void FollowMainPlayer(Player player) {
        if (!npc.getEntity().getWorld().equals(player.getWorld())) {
            npc.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }
        if (!npc.getNavigator().isNavigating() && npc.getEntity().getLocation().distance(player.getLocation()) >= margin) {
            npc.getNavigator().setTarget(player.getLocation());
        } else {
            if (npc.getEntity().getLocation().distance(player.getLocation()) <= margin)
            {
                npc.getNavigator().cancelNavigation();
            }
            flock.run();
        }
    }

    @Persist("savedState")
    public NPCState savedState = NPCState.NOTHING;
    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {

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

        this.setState(savedState);
    }


    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }
}