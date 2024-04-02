package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.trysomethingdev.devcraft.UserChatMessageToCommand.ProcessChatMessageFromSender;

public class DevCraftTwitchUsersManager {

    private final DevCraftPlugin plugin;
    //List of Twitch Users here
    List<DevCraftTwitchUser> devCraftTwitchUsers = new ArrayList<DevCraftTwitchUser>();
    private Location globalNpcSpawnPoint;

    public DevCraftTwitchUsersManager(DevCraftPlugin devCraftPlugin,Location npcGlobalSpawnPoint) {

        plugin = devCraftPlugin;
        this.globalNpcSpawnPoint = npcGlobalSpawnPoint;
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
            this.Add(new DevCraftTwitchUser(joinedNick,joinedNick,globalNpcSpawnPoint));
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
        var user = getUserByTwitchUserName(sender.getUserName());
        ProcessChatMessageFromSender(sender, message, user,plugin);

        if(user == null) this.Add(new DevCraftTwitchUser(sender.getUserName(),sender.getUserName(),globalNpcSpawnPoint));
        else user.Chatted();
    }

    public void userParted(String partedNick) {
        var user = getUserByTwitchUserName(partedNick);
        if(user != null) user.Parted();
    }
}
