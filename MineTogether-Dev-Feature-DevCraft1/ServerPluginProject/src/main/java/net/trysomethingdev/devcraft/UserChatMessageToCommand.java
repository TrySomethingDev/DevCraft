package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class UserChatMessageToCommand {
    static void ProcessChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        if(user == null) return;

        Bukkit.getLogger().info("ReadingMessage");
        Bukkit.getLogger().info(message.getContent());

        if(message.getContent().startsWith("!"))
        {
            Bukkit.getLogger().info("This has message has been identified as a command");
            var command = message.getContent().toUpperCase();
            if(command.startsWith("!SKIN")) ExecuteChangeUserSkinCommand(sender, command, user);
            if(command.startsWith("!JOIN") || command.startsWith("!PLAY")) ExecuteJoinCommand(sender, user);
            //  if(command.startsWith("!EXIT") || command.startsWith("!QUIT")) ExecuteExitCommand(sender);
            //    if(command.startsWith("!GOTOBED") || command.startsWith("!BED") || command.startsWith("!SLEEP")) ExecuteGoToBedCommand(sender);
            if(command.startsWith("!FISH")) ExecuteFishCommand(sender, user);
            //   if(command.startsWith("!MINE")) ExecuteMineCommand(sender);
            //   if(command.startsWith("!LOG") || command.startsWith("!CHOP")  || command.startsWith("!WOOD")) ExecuteLogCommand(sender);
            if(command.startsWith("!EAT")) ExecuteEatCommand(sender, user);
            if(command.startsWith("!TEST")) ExecuteTestCommand(sender, user);
            //  if(command.startsWith("!BUILD")) ExecuteBuildCommand(sender);
            if(command.startsWith("!QUARRY")) ExecuteQuarryCommand(sender,command, user,plugin);
            if(command.startsWith("!DANCE")) ExecuteDanceCommand(sender,command, user);
            if(command.startsWith("!FOLLOW")) ExecuteFollowCommand(sender,command, user);
            //  if(command.startsWith("!UNLOAD")
            //      || command.startsWith("!EMPTY")
            //      || command.startsWith("!CLEAR")
            //          || command.startsWith("!EMPTYINV")) ExecuteUnloadIntoNearestChest(sender,command);
            if(command.startsWith("!RESPAWN")) ExecuteRespawnCommand(sender,command,user);




        }
        else {
            Bukkit.getLogger().info("Not identified as a command");
        }
    }

    private static void ExecuteRespawnCommand(TwitchUser sender, String command, DevCraftTwitchUser user) {
        user.Respawn();
    }

    private  static void ExecuteUnloadIntoNearestChest(TwitchUser sender, String command, DevCraftTwitchUser user) {
          user.UnloadIntoNearestChest();
    }

    private  static void ExecuteFollowCommand(TwitchUser sender, String command, DevCraftTwitchUser user) {
         user.FollowCommand();
    }

    private static void ExecuteDanceCommand(TwitchUser sender, String command, DevCraftTwitchUser user) {


        user.DanceCommand();
    }

    private static void ExecuteQuarryCommand(TwitchUser sender, String command, DevCraftTwitchUser user,DevCraftPlugin plugin) {

        var splitStringList = command.split(" ");
        if (Arrays.stream(splitStringList).count() != 4)
        {
            //Just do default 1x1x1
            user.QuarryCommand(80,80,400,plugin);
        }
        else {

            int length = 80, width = 80,depth = 400;
            try {
                length = Integer.parseInt(splitStringList[1]);
                width =  Integer.parseInt(splitStringList[2]);
                depth =  Integer.parseInt(splitStringList[3]);
            }
            catch( Exception e){

            }
            user.QuarryCommand(length,width,depth,plugin);
        }
    }



    private static void ExecuteFishCommand(TwitchUser sender, DevCraftTwitchUser user) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Fish Mode ");
        user.StartFishingCommand();
    }

    private static void ExecuteMineCommand(TwitchUser sender, DevCraftTwitchUser user) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Mine Mode ");
        user.StartMineCommand();
    }

    private static void ExecuteLogCommand(TwitchUser sender, DevCraftTwitchUser user) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Log Mode ");
        user.StartLoggingTreesCommand();
    }

    private static void ExecuteEatCommand(TwitchUser sender, DevCraftTwitchUser user) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Eat Mode ");
        user.StartEatingCommand();
    }

    private static void ExecuteBuildCommand(TwitchUser sender, DevCraftTwitchUser user) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Build Mode ");
        user.StartBuildingCommand();
    }

    private static void ExecuteTestCommand(TwitchUser sender, DevCraftTwitchUser user) {
        user.StartFishingCommand();
    }

    private static void ExecuteGoToBedCommand(TwitchUser sender, DevCraftTwitchUser user) {
    user.userWantsToPlay = true;
    }

    private static void ExecuteExitCommand(TwitchUser sender, DevCraftTwitchUser user) {
        user.userWantsToPlay = false;
    }

    private static void ExecuteJoinCommand(TwitchUser sender, DevCraftTwitchUser user) {
      user.userWantsToPlay = true;
    }

    private static void ExecuteChangeUserSkinCommand(TwitchUser sender, String command, DevCraftTwitchUser user) {
        var splitStringList = command.split(" ");
        //The Skin name we are going to use is the second word.
        var skin = splitStringList[1];
            user.changeSkin(skin);
    }
}
