package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.FollowCustomTrait;
import net.trysomethingdev.devcraft.traits.MinerTrait;
import net.trysomethingdev.devcraft.traits.ThrowItemTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.util.Vector;

public class MineCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {

        new DelayedTask(() -> {
            //Have NPC Appear Behind wherever Player is...

            var mainPlayerName = plugin.getMainPlayerUserName();
            var playerEntity = Bukkit.getPlayer(mainPlayerName);

            var playerLocation = playerEntity.getLocation();

            var playerFacingDirection = playerEntity.getLocation().getDirection();

            var locationBehindPlayer = getSafeLocationBehindPlayer(playerEntity);
//
//            var combinedString = "We think Dev is facing " + playerFacingDirection;
//            Bukkit.broadcastMessage(combinedString);

            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
              //  if (npc == null) Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                //return;

                npcHelper.getOrCreateNPCAndSpawnIt(user,locationBehindPlayer);
            }
            else{
                npc.teleport(locationBehindPlayer, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }




           //Try to make seem like they are catching up with you.
            ;

            //npc4.getOrAddTrait(FollowCustomTrait.class);



            //NPC Throw down a item frame and a wood pickaxe

            npc.getOrAddTrait(ThrowItemTrait.class);

            //Wait for Player to place Item Frame and Pickaxe

            //Find Item Frame and PickAxe

            // Start Mining

            //When finished mining

            //Appear behind player again.

            //Place down chest

            //Put everything mined in chest

            //Wait for player to open chest.

            // When player closes chest.

            //NPC picks up chest and remaining items

            //Take them to main camp and stores them.

           // npc.getOrAddTrait(MinerTrait.class);
        }, 20);

     //  var playerEntity = Bukkit.getPlayer(plugin.getMainPlayerUserName());
        //Have NPC make some Attention Getting sounds

       // PlayAlertSound(playerEntity,Sound.BLOCK_NOTE_BLOCK_BASS,20);
       // PlayAlertSound(playerEntity,Sound.BLOCK_NOTE_BLOCK_BELL,30);

    }



    private static void PlayAlertSound(Player playerEntity,Sound sound,int ticksToWait) {

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
}
