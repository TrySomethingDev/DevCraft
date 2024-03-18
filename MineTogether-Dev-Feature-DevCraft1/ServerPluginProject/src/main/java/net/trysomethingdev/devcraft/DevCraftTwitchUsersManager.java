package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevCraftTwitchUsersManager {

    private final DevCraftPlugin plugin;
    //List of Twitch Users here
    List<DevCraftTwitchUser> devCraftTwitchUsers = new ArrayList<DevCraftTwitchUser>();

    public DevCraftTwitchUsersManager(DevCraftPlugin devCraftPlugin) {

        plugin = devCraftPlugin;

        LoadSavedList();
        SaveThisListToConfigEverySoManyMinutes(0.5);


    }

    private void LoadSavedList() {

        Gson gson = new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        // Load from file
        try (Reader reader = new FileReader("DevCraftTwitchUsers.json")) {
            devCraftTwitchUsers = gson.fromJson(reader, new TypeToken<List<DevCraftTwitchUser>>(){}.getType());
            // Now loadedUsers contains the users that were saved to the file
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void Save()
    {
        Bukkit.getLogger().info("PRINTED LIST****");
        for(var foo : devCraftTwitchUsers)
        {
           Bukkit.getLogger().info(foo.twitchUserName);
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        // Save to file
        try (Writer writer = new FileWriter("DevCraftTwitchUsers.json")) {
            gson.toJson(devCraftTwitchUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SaveThisListToConfigEverySoManyMinutes(double minutesBetweenSaves) {
        new DelayedTask(() -> {

            Save();
            SaveThisListToConfigEverySoManyMinutes(0.5);


        }, (long) (20 * (minutesBetweenSaves * 60)));

    }

    public void DespawnTwitchUsersWhoHaveBeenInactiveTooLong() {

        new DelayedTask(() -> {

            for(var user : devCraftTwitchUsers)
            {
                Duration duration = Duration.between(user.lastActivityTime, LocalDateTime.now());
                long minutes = duration.toMinutes();

                if(user.markedForDespawn)
                {
                    NPC npc = user.GetUserNPC();
                    if(npc != null && npc.isSpawned())
                    {
                        npc.despawn();
                    }
                }

                else if(minutes > 20 && user.markedForDespawn)
                {
                    NPC npc = user.GetUserNPC();
                    if(npc != null && npc.isSpawned())
                    {
                        npc.despawn();
                    }
                }
                else if (minutes > 19 && !user.markedForDespawn)
                {
                    user.markUserForDespawn();
                }

            }

            //Call it again so it checks again after so much time.
            DespawnTwitchUsersWhoHaveBeenInactiveTooLong();


        }, 20 * 20);
    }


    //Add
public void Add(DevCraftTwitchUser twitchUser){
    if (!devCraftTwitchUsers.contains(twitchUser))
    {
        devCraftTwitchUsers.add(twitchUser);
    }
}

    //Remove
    public void Remove(DevCraftTwitchUser twitchUser){
        if (!devCraftTwitchUsers.contains(twitchUser))
        {
            devCraftTwitchUsers.remove(twitchUser);
        }
    }


    public void userJoined(String joinedNick) {
        var user = getUserByTwitchUserName(joinedNick);
        if(user == null)
        {  //If we make it here it means we did not find the user in the list. So we should add a user.
            this.Add(new DevCraftTwitchUser(joinedNick,joinedNick));
        }
        else
        {
            user.JustJoinedOrIsActive();
        }



    }

    public DevCraftTwitchUser getUserByTwitchUserName(String joinedNick) {
        for (var user : devCraftTwitchUsers)
        {
            if(joinedNick.toUpperCase().equals(user.twitchUserName.toUpperCase())) {
                return user;
            }
        }
        return null;
    }

    public void userChatted(TwitchUser sender, TwitchMessage message) {

        //What kind of Command is this?

        //!SKIN MINECRAFTNAME
        Bukkit.getLogger().info("ReadingMessage");
        Bukkit.getLogger().info(message.getContent());

        if(message.getContent().startsWith("!"))
        {
            Bukkit.getLogger().info("This has message has been identified as a command");
            var command = message.getContent().toUpperCase();
            if(command.startsWith("!SKIN")) ExecuteChangeUserSkinCommand(sender, command);
            if(command.startsWith("!JOIN") || command.startsWith("!PLAY")) ExecuteJoinCommand(sender);
            if(command.startsWith("!EXIT") || command.startsWith("!QUIT")) ExecuteExitCommand(sender);
            if(command.startsWith("!GOTOBED") || command.startsWith("!BED") || command.startsWith("!SLEEP")) ExecuteGoToBedCommand(sender);
            if(command.startsWith("!FISH")) ExecuteFishCommand(sender);
            if(command.startsWith("!MINE")) ExecuteMineCommand(sender);
            if(command.startsWith("!LOG") || command.startsWith("!CHOP")  || command.startsWith("!WOOD")) ExecuteLogCommand(sender);
            if(command.startsWith("!EAT")) ExecuteEatCommand(sender);
            if(command.startsWith("!TEST")) ExecuteTestCommand(sender);
            if(command.startsWith("!BUILD")) ExecuteBuildCommand(sender);
            if(command.startsWith("!QUARRY")) ExecuteQuarryCommand(sender,command);




        }
        else {
            Bukkit.getLogger().info("Not identified as a command");
        }

        var user = getUserByTwitchUserName(sender.getUserName());
        if(user == null) this.Add(new DevCraftTwitchUser(sender.getUserName(),sender.getUserName()));
        else user.Chatted();
    }

    private void ExecuteQuarryCommand(TwitchUser sender, String command) {

        var user = getUserByTwitchUserName(sender.getUserName());
        if(user == null) return;

        var splitStringList = command.split(" ");
        if (Arrays.stream(splitStringList).count() != 4)
        {
            //Just do default 1x1x1
            user.QuarryCommand(1,1,1);
        }
        else {

            int length = 1, width = 1,depth = 1;
            try {
                 length = Integer.parseInt(splitStringList[1]);
                 width =  Integer.parseInt(splitStringList[2]);
                 depth =  Integer.parseInt(splitStringList[3]);
            }
            catch( Exception e){

            }

            user.QuarryCommand(length,width,depth);
            }

        }



    private void ExecuteFishCommand(TwitchUser sender) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Fish Mode ");
        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartFishingCommand();
    }

    private void ExecuteMineCommand(TwitchUser sender) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Mine Mode ");
        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartMineCommand();
    }

    private void ExecuteLogCommand(TwitchUser sender) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Log Mode ");
        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartLoggingTreesCommand();
    }

    private void ExecuteEatCommand(TwitchUser sender) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Eat Mode ");
        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartEatingCommand();
    }

    private void ExecuteBuildCommand(TwitchUser sender) {
        Bukkit.getLogger().info("User " + sender.getUserName() + "tried to Activate Build Mode ");
        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartBuildingCommand();
    }

    private void ExecuteTestCommand(TwitchUser sender) {

        var user = getUserByTwitchUserName(sender.getUserName());
        user.StartFishingCommand();
        Bukkit.getLogger().info("Test Command Logged");


    }

    private void ExecuteGoToBedCommand(TwitchUser sender) {
        var user = getUserByTwitchUserName(sender.getUserName());
        if(user != null) user.userWantsToPlay = true;

    }

    private void ExecuteExitCommand(TwitchUser sender) {
        var user = getUserByTwitchUserName(sender.getUserName());
        if(user != null) user.userWantsToPlay = false;
    }

    private void ExecuteJoinCommand(TwitchUser sender) {
        var user = getUserByTwitchUserName(sender.getUserName());
        if(user != null) user.userWantsToPlay = true;
    }

    private void ExecuteChangeUserSkinCommand(TwitchUser sender, String command) {
        var splitStringList = command.split(" ");
        //The Skin name we are going to use is the second word.
        var skin = splitStringList[1];
        var user = getUserByTwitchUserName(sender.getUserName());
        if(user != null){
            user.changeSkin(skin);
        }
    }

    public void userParted(String partedNick) {
        var user = getUserByTwitchUserName(partedNick);
        if(user != null) user.Parted();
    }
}
