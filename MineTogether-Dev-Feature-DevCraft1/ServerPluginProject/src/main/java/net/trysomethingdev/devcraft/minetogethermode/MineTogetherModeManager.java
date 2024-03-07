package net.trysomethingdev.devcraft.minetogethermode;




import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import net.trysomethingdev.devcraft.TrySomethingDevAmazingPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.Integer.parseInt;


public class MineTogetherModeManager {

    TrySomethingDevAmazingPlugin _plugin;
String _yourMCPlayerName;
    public MineTogetherModeManager(TrySomethingDevAmazingPlugin plugin,String yourMCPlayerName) {
        _plugin = plugin;
_yourMCPlayerName = yourMCPlayerName;
    }

    private Boolean isStarted = false;
    //We are going to need


    //Functions Needed


    //Create mineTogether mode chest

    //give player special Minetogether mode chest

    // Watch for Chest being placed that is a special mineTogether Mode chest
    public void start(Chest chest) {
        isStarted = true;
        Bukkit.getLogger().info("Starting MineTogether Mode");

        //Send message to Chat
        // Mine Together Mode has been activated
        SendMTMStartingMessageToChat();
       // StartNextStepAfterSeconds(60);
        var block = chest.getBlock();




        //By replacing the block with air, it seems to clear all of the custom aspects of the chest.
        //WHen I tried just replacing the block with a chest it did not seem to replace it with a plain chest.
        block.setType(Material.AIR);
        block.setType(Material.CHEST);


//        ItemStack item = new ItemStack(Material.CHEST, 1);
//        ItemMeta meta = item.getItemMeta();
//        meta.displayName(Component.text("Mine Together Mode Chest"));
//        List<String> lore22 = new ArrayList<>();
//
//        List<Component> lore = new ArrayList<>(); lore.add(Component.text("Place this chest on the ground to")); lore.add(Component.text("start Mine Together Mode"));
//        meta.lore(lore);
//        meta.addEnchant(Enchantment.LUCK,1 ,false);
//        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//
//        item.setItemMeta(meta);
//        chest = item;

        StartMiningModeAfterSoManySeconds(60,chest);
        // !mine
        // !mine 100
        // !mine 100 SkinName

        //After 60 Seconds Spawn Miners and have the start mining

        //Have Miners Keep Track of their Inventory


        //Have Miners place Get Paid for what they collected

        //Have miners place paid for items into chest.

        //Place log book in the chest.


        //Send Message to chat announcing earnings
        //Send Message to chat announcing mine together mode is complete.

        //Clear Miner List from MineTogether Mode list




//    for (int i = 0; i < 10; i++) {
//        MoveFirstNPCOnListOneBlock();
//
//    }
        //Once minetogethermode is activated send message to twitch chat
        // "Mine together mode has been activated type "!mine" to join."

        //Twitch Chat Integration After we get it working without
        //Wait X seconds (Configurable)
        //Read chat to see who should be added to mine together mode.

        //For each person that wants to join mine together mode add them to list of people to join.
        //Add each person to the world as an NPC
        //After a set amount of time start mining


        //Default we will mine 4 blocks (Later this will be configurable from twitch chat).
        //For each npc that is mining have them mine the blocks in front of them and move forward one block.
        //Make sure to add to their inventory each block that they mine.

        //At the end of the mine together mode have all players place items in the chest.
        //Remove all NPC from the world.
        //Convert Chest into standard Chest and mark Mine Together mode as complete
        //We convert it to a standard chest so that the player cannot simply reactivate mine together mode again

        //Award each twitch chat player with channel points for what they mined.

        //by picking it up and placing it down.


    }

    private  void StartMiningModeAfterSoManySeconds(int seconds,Chest chest) {

        for (int i = 0; i < seconds; i++) {
            int finalI = seconds - i;
            new DelayedTask(() -> {

                BukkitCommand.broadcastCommandMessage(Bukkit.getPlayer(_yourMCPlayerName),"" + finalI + " seconds until mining starts" );

            }, 20 * i);
        }





        new DelayedTask(() -> {

            Bukkit.getLogger().info("Seconds have passed and we are starting the mining");


            String directionToMine = getDirectionToMine(chest);

            var listOfPLayers = getListOfChattersThatWantToHelpMine();

            assert listOfPLayers != null;

            if(listOfPLayers.stream().count() == 0 )
            {
                Bukkit.getLogger().info("No Miners on the List");
                BukkitCommand.broadcastCommandMessage(Bukkit.getPlayer(_yourMCPlayerName),"No Miners volunteered.");
            }
            else {
                Bukkit.getLogger().info("Found " + listOfPLayers.stream().count() + " miners on the list");
                BukkitCommand.broadcastCommandMessage(Bukkit.getPlayer(_yourMCPlayerName), "Found " + listOfPLayers.stream().count() + " miners on the list");
                BukkitCommand.broadcastCommandMessage(Bukkit.getPlayer(_yourMCPlayerName),"Loading Miners");
            }


            int counter = 0;
            for (int i = 0; i < listOfPLayers.size(); i++) {
                String playerNameSkinNameAndNumberOfBlocks = listOfPLayers.get(i);


                _plugin.reloadConfig();


                List<String> playerInfo = Arrays.asList(playerNameSkinNameAndNumberOfBlocks.split(","));

                for (int i1 = 0; i1 < playerInfo.size(); i1++) {
                    Bukkit.getLogger().info(playerInfo.get(i1));
                }
//                Random rn = new Random();
//                int numberOfBlocksToMine = rn.nextInt(5,100);


                String playerName = playerInfo.get(0);
                int numberOfBlocksToMine = parseInt(playerInfo.get(1));
                String playerSkinName = playerInfo.get(2);



                Location chestLocation = chest.getLocation();
                //Spawn in a player Entity
                //Place the Player Entity in the correct Starting position.

                counter = counter + 1;

                Location location = CalculateLocationToPlacePlayerEntity(chestLocation, directionToMine);

                createNPCMiner(location, directionToMine, playerName,playerSkinName,numberOfBlocksToMine,counter);

            }


        }, 20 * seconds);
    }
    private void SendMTMStartingMessageToChat() {

        //Trigger Node.JS Endpoint


        _plugin.reloadConfig();

        var config = _plugin.getConfig();
        //Set General:RequestedAction to "StartRequested"
        String requestedAction = config.getString("General.RequestedAction");

        Bukkit.getLogger().info(requestedAction);
        _plugin.getConfig().set("General.RequestedAction", "StartRequested");

        _plugin.saveConfig();



    }

    @Nullable
    private List<String> getListOfChattersThatWantToHelpMine() {

//        Bukkit.reload();
//        try {
//
//
//            _plugin.getConfig().load("C:\\Files\\Servers\\PaperServerDev1\\plugins\\TrySomethingDevAmazingPlugin\\config.yml");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        _plugin.reloadConfig();

        var listOfPLayers = (List<String>) _plugin.getConfig().getList("ChattersThatWantToPlay");
        return listOfPLayers;
    }

    @NotNull
    private String getDirectionToMine(Chest chest) {
        String directionChestIsFacing = getChestFacingDirection(chest.getBlockData());
        return GetOppositeCompassDirectioFromStringToString(directionChestIsFacing);

    }


    private Location CalculateLocationToPlacePlayerEntity(Location chestLocation, String directionToMine) {
        return switch (directionToMine) {
            case "north" ->  chestLocation.add(2.5, 0, 0.5);
            case "south" -> chestLocation.add(-1.5, 0, 0.5);
            case "east" -> chestLocation.add(0.5, 0, 0.5);
            case "west" -> chestLocation.add(0.5, 0, -1.5);
            default -> chestLocation;
        };
    }

    private static String GetOppositeCompassDirectioFromStringToString(String originalDirection) {
        return switch (originalDirection) {
            case "north" -> "south";
            case "south" -> "north";
            case "east" -> "west";
            case "west" -> "east";
            default -> "";
        };
    }


    private static void MoveFirstNPCOnListOneBlock() {
        var list = FancyNpcsPlugin.get().getNpcManager().getAllNpcs();
        Optional<Npc> npc2 = list.stream().findFirst();
        if (npc2.isEmpty()) {
            return;
        }
        var npc = npc2.get();
        var data = npc.getData();
        var location2 = data.getLocation();
        Location location3 = location2.add(1, 0, 0);
        data.setLocation(location3);

        npc.moveForAll();
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
    }

    private static void MoveNPCToSpecificLocation(Location location, String directionToFace, Npc npc, Integer counter) {
        var data = npc.getData();

        float yawValue = GetYawFromDirectionToFace(directionToFace);

        location.setYaw(yawValue);


        data.setLocation(location.add(0, 0, counter));
        npc.moveForAll();
    }

    private static float GetYawFromDirectionToFace(String directionToFace) {

        float yawValue = 0f;
        if (directionToFace == "north") yawValue = -180f;
        if (directionToFace == "south") yawValue = 0f;
        if (directionToFace == "east") yawValue = -90f;
        if (directionToFace == "west") yawValue = 90f;

        return yawValue;


    }


    private void createNPCMiner(Location startingLocation, String directionFacing, String playerName,  String playerSkinName, int numberOfBlocksToMine,int counter) {

if (playerSkinName == null || playerSkinName.equals("")){
             playerSkinName = "Player";
}


//TO DO IT NEEDS THE PLAYERS NAME HERE NOT SURE HOW NOT TO HARDCODE IT
        Player player = Bukkit.getPlayer(_yourMCPlayerName);


        NpcData data = new NpcData(playerName,player.getUniqueId(), startingLocation.add(0, 0, counter));

        data.setSkin(GetSkinToUse(playerSkinName));
        data.setDisplayName(playerName);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        npc.create();
        npc.spawnForAll();

        MoveNPCToSpecificLocation(startingLocation, directionFacing, npc, 0);
        if (numberOfBlocksToMine == 0) numberOfBlocksToMine = 2;
        var numberOfStepsToTake = numberOfBlocksToMine / 2;
        var numberOfBlocksToMove = numberOfBlocksToMine / 2;
        var numberOfMoves = numberOfBlocksToMove * 10;

        for (int i = 0; i < numberOfStepsToTake; i++) {
            MoveNPCInXDirectionAfterSeconds(npc, i);

        }
        var secondsToWait = numberOfBlocksToMine * 2;
        RemoveNPCFromWorldAfterSoManySeconds(npc,secondsToWait);

    }


    @NotNull
    private static SkinFetcher GetSkinToUse(String playerSkinName) {
        var UUID = "Nothing";
        UUID = SkinManager.GetUUIDForMinecraftUsername(playerSkinName);
        //If UUID comes back null. We will use this default "player" skin.
        if (UUID == null) { UUID = "756300a9b13f4823aac6c8f8fb287fd7";}
        SkinFetcher skin = new SkinFetcher(UUID);
        return skin;
    }


    private static boolean IsThereMinableBlockAtHeadLevelAndBreakIt(Npc npc) {

        float eyeHeight = npc.getEyeHeight();
        //  Bukkit.getLogger().info("EyeHeight =" + eyeHeight);
        var direction = npc.getData().getLocation().getDirection();

        //   Bukkit.getLogger().info("Direction");
        //  Bukkit.getLogger().info(direction.toString());

        var location = npc.getData().getLocation();
        //  Bukkit.getLogger().info("location to string:" + location.toString() );

        //   Bukkit.getLogger().info("Get Block at location to string:" + location.getBlock().toString() );

        // Get Block at plus 1 x and eye height
        Location locationPlus1x = location.add(.5, 0, 0);
        BreakBlocksAtLocation(location, locationPlus1x);
//
//        locationPlus1x = location.add(1, 0, 0);
//        BreakBlocksAtLocation(location, locationPlus1x);
//        // Bukkit.getLogger().info("Get Block at location  to string:" + location.getBlock().toString() );
//
//        locationPlus1x = location.add(.2, 0, 0);
//        BreakBlocksAtLocation(location, locationPlus1x);
//        // Bukkit.getLogger().info("Get Block at location  to string:" + location.getBlock().toString() );


        return false;
    }

    private static void BreakBlocksAtLocation(Location location, Location locationPlus1x) {
        Location locationPlus1xAndEyeHeight = new Location(location.getWorld(), locationPlus1x.getX(), locationPlus1x.getY() + 1, locationPlus1x.getZ());
        var block = locationPlus1xAndEyeHeight.getBlock();

        block.breakNaturally();

        Location locationPlus1xAndKneeHeight = new Location(location.getWorld(), locationPlus1x.getX(), locationPlus1x.getY(), locationPlus1x.getZ());
        var block2 = locationPlus1xAndKneeHeight.getBlock();
        //  Bukkit.getLogger().info("Get Block at KneeHeight Plus 1 x  to string:" + block2.toString() );


        new DelayedTask(() -> {
            block2.breakNaturally();

        }, 20 * 1);
    }

    private static void MoveNPCInXDirectionAfterSeconds(Npc npc, int seconds) {
        Random rn = new Random();
        long i = rn.nextLong(2,4);
        long randomTime = (long)seconds * i;

        new DelayedTask(() -> {
            if (IsThereMinableBlockAtHeadLevelAndBreakIt(npc)) {
                // Bukkit.getLogger().info("Found Mineable Block at Head Level");
            } else {
                //  Bukkit.getLogger().info("No Mineable Block at Head Level");
            }

            new DelayedTask(() -> {
                npc.getData().setLocation(
                    npc.getData().getLocation().add(.05, 0, 0)
            );
            npc.moveForAll();
                if (IsThereMinableBlockAtHeadLevelAndBreakIt(npc)) {
                    // Bukkit.getLogger().info("Found Mineable Block at Head Level");
                } else {
                    //  Bukkit.getLogger().info("No Mineable Block at Head Level");
                }

            }, 20 * randomTime);

        }, 20 * randomTime);
    }

    private static void RemoveNPCFromWorldAfterSoManySeconds(Npc npc, int seconds) {
        new DelayedTask(() -> {
            npc.removeForAll();
            FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
        }, 20 * seconds);
    }


    private String getChestFacingDirection(BlockData blockData) {
        String blockDataString = blockData.getAsString();
        String direction = "north";

        if (blockDataString.contains("west")) direction = "west";
        else if (blockDataString.contains("east")) direction = "east";
        else if (blockDataString.contains("north")) direction = "north";
        else if (blockDataString.contains("south")) direction = "south";

        Bukkit.getLogger().info("chest is facing");
        Bukkit.getLogger().info(direction);
        return direction;
    }

}

