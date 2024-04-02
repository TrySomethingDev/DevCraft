package net.trysomethingdev.devcraft.handlers;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.DevCraftTwitchUsersManager;
import org.bukkit.Bukkit;

import java.util.Collection;

public class DevCraftChatHandler {


    private final DevCraftPlugin plugin;
    private final DevCraftTwitchUsersManager usersManager;

    public DevCraftChatHandler(DevCraftPlugin devCraftPlugin) {
        plugin = devCraftPlugin;
        usersManager = plugin.getTwitchUsersManager();
    }

    public void handlChat(TwitchUser sender, TwitchMessage message) {
        Bukkit.getLogger().info("Chat has occured:" + message.getCommand());
        //UserId
        //TwitchName
        //MinecraftSkinName
        //TimeLastMessageReceived

        usersManager.userChatted(sender,message);
        Bukkit.getLogger().info("End of handleChat: " + message.getContent());


    }

    public void handleOnNamesList(Collection<String> namesList) {
        Bukkit.getLogger().info("Names List event fired******");
        Bukkit.getLogger().info("Printing Names List");


        for (var name : namesList) {
            Bukkit.getLogger().info(name);
            usersManager.Add(new DevCraftTwitchUser(plugin,name,name,plugin.getNpcGlobalSpawnPoint()));
        }

    }

    public  void handleOnJoin(String joinedNick) {
        Bukkit.getLogger().info("Joined event fired******");
        Bukkit.getLogger().info(joinedNick);
        usersManager.userJoined(joinedNick);

    }

    public  void handleOnPart(String partedNick) {
        Bukkit.getLogger().info("Parted event fired******");
        Bukkit.getLogger().info(partedNick);
        usersManager.userParted(partedNick);
        //At this time we dont want to do anything with this.

    }
}
