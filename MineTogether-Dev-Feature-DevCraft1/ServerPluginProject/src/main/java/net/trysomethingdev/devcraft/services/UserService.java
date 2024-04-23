package net.trysomethingdev.devcraft.services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.*;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserService {

    private final DevCraftPlugin plugin;
    List<DevCraftTwitchUser> devCraftTwitchUsers = new ArrayList<DevCraftTwitchUser>();

    public UserService(DevCraftPlugin devCraftPlugin) {
        plugin = devCraftPlugin;
        LoadSavedList();
        SaveThisListToConfigEverySoManyMinutes(0.1);
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

    public DevCraftTwitchUser getUserByTwitchUserName(String twitchUserName) {
        for (var user : devCraftTwitchUsers)
        {
            if(twitchUserName.equalsIgnoreCase(user.twitchUserName)) {
                return user;
            }
        }
        return null;
    }

    public DevCraftTwitchUser getOrAddUser(String userName) {
        var user = this.getUserByTwitchUserName(userName);
        if(user == null)
        {
            this.Add(new DevCraftTwitchUser(userName,userName));
            user = this.getUserByTwitchUserName(userName);
        }
        return user;
    }










}
