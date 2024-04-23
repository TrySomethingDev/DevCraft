package net.trysomethingdev.devcraft.services;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.command.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class UserChatMessageToCommandService {

    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final DevCraftPlugin plugin;

    public UserChatMessageToCommandService(DevCraftPlugin devCraftPlugin) {

        plugin = devCraftPlugin;

        commandRegistry.put("!SKIN", new SkinChangeCommand());
        commandRegistry.put("!JOIN", new JoinCommand());
        commandRegistry.put("!RESPAWN", new RespawnCommand());
        commandRegistry.put("!EMPTY", new EmptyInventoryCommand());
        commandRegistry.put("!FOLLOW", new FollowPlayerCommand());
        commandRegistry.put("!DANCE", new DanceCommand());
        commandRegistry.put("!FISH", new FishingCommand());
        commandRegistry.put("!MINE", new MineCommand());
        commandRegistry.put("!QUARRY", new QuarryCommand());
        commandRegistry.put("!EXIT", new ExitCommand());
        commandRegistry.put("!WAVE", new WaveCommand());
    }

    public void processChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        if (user == null) return;

        Bukkit.getLogger().info("Reading Message");
        Bukkit.getLogger().info(message.getContent());

        if (message.getContent().startsWith("!")) {
            var contentToUpperCase = message.getContent().toUpperCase();
            var contentSplitArray = contentToUpperCase.split(" ");
            var firstWord = contentSplitArray[0];
            Command command = commandRegistry.get(firstWord);
            if (command != null) {
                command.execute(sender, message, user, plugin);
            } else {
                Bukkit.getLogger().info("Unknown command: " + firstWord);
            }
        } else {
            Bukkit.getLogger().info("Not identified as a command");
        }
    }
}







