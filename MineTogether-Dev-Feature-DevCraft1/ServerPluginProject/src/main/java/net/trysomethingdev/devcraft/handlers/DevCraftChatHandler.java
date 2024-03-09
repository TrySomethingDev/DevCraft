package net.trysomethingdev.devcraft.handlers;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.bukkit.Bukkit;

import java.util.Collection;

public class DevCraftChatHandler {


    public static void handlChat(TwitchUser sender, TwitchMessage message) {
        Bukkit.getLogger().info("Chat has occured:" + message.getCommand());
        //UserId
        //TwitchName
        //MinecraftSkinName
        //TimeLastMessageReceived



    }

    public static void handleOnNamesList(Collection<String> namesList) {
        Bukkit.getLogger().info("Names List event fired******");
        Bukkit.getLogger().info("Printing Names List");
        for (var name : namesList) {
            Bukkit.getLogger().info(name);
        }

    }

    public static void handleOnJoin(String joinedNick) {
        Bukkit.getLogger().info("Joined event fired******");
        Bukkit.getLogger().info(joinedNick);

    }

    public static void handleOnPart(String partedNick) {
        Bukkit.getLogger().info("Parted event fired******");
        Bukkit.getLogger().info(partedNick);
    }
}
