package net.trysomethingdev.devcraft;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.trysomethingdev.devcraft.util.DelayedTask;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DevCraftTwitchUsersManager {

    private final DevCraftPlugin plugin;
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

    public DevCraftTwitchUser getUserByTwitchUserName(String joinedNick) {
        for (var user : devCraftTwitchUsers)
        {
            if(joinedNick.equalsIgnoreCase(user.twitchUserName)) {
                return user;
            }
        }
        return null;
    }


    public void userParted(String partedNick) {
        var user = getUserByTwitchUserName(partedNick);
        if(user != null) user.Parted();
    }

    public DevCraftTwitchUser getOrAddUser(String userName) {
        var user = this.getUserByTwitchUserName(userName);
        if(user == null)
        {
            this.Add(new DevCraftTwitchUser(plugin,userName,userName, plugin.getNpcGlobalSpawnPoint()));
            user = this.getUserByTwitchUserName(userName);
        }

        return user;
    }
}
