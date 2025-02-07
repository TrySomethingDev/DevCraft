package net.trysomethingdev.devcraft.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.DelayedTask;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class UserService {

    private final Set<DevCraftTwitchUser> devCraftTwitchUsers = new HashSet<>();
    private static final String FILE_NAME = "DevCraftTwitchUsers.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public UserService(DevCraftPlugin devCraftPlugin) {
        loadSavedList();
        scheduleAutoSave(0.1);
    }

    private void loadSavedList() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Set<DevCraftTwitchUser> loadedUsers = GSON.fromJson(reader, new TypeToken<Set<DevCraftTwitchUser>>() {}.getType());
            if (loadedUsers != null) {
                devCraftTwitchUsers.addAll(loadedUsers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            GSON.toJson(devCraftTwitchUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scheduleAutoSave(double minutesBetweenSaves) {
        new DelayedTask(this::save, (long) (20 * (minutesBetweenSaves * 60)));
    }

    public void addUser(DevCraftTwitchUser twitchUser) {
        devCraftTwitchUsers.add(twitchUser);
    }

    public void removeUser(DevCraftTwitchUser twitchUser) {
        devCraftTwitchUsers.remove(twitchUser);
    }

    public DevCraftTwitchUser getUserByTwitchUserName(String twitchUserName) {
        return devCraftTwitchUsers.stream()
                .filter(user -> twitchUserName.equalsIgnoreCase(user.twitchUserName))
                .findFirst()
                .orElse(null);
    }

    public DevCraftTwitchUser getOrAddUser(String userName) {
        return devCraftTwitchUsers.stream()
                .filter(user -> userName.equalsIgnoreCase(user.twitchUserName))
                .findFirst()
                .orElseGet(() -> {
                    DevCraftTwitchUser newUser = new DevCraftTwitchUser(userName, userName);
                    devCraftTwitchUsers.add(newUser);
                    return newUser;
                });
    }
}