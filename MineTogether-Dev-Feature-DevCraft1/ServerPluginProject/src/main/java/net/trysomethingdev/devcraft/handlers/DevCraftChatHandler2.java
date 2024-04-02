package net.trysomethingdev.devcraft.handlers;

public class DevCraftChatHandler2 {
}

package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class UserChatMessageToCommand {
    private final Map<String, Command> commandRegistry = new HashMap<>();

    public UserChatMessageToCommand() {
        // Initialize the command registry
        commandRegistry.put("!SKIN", new SkinChangeCommand());
        commandRegistry.put("!JOIN", new JoinCommand());
        // Add more commands as needed
    }

    public void processChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        if (user == null) {
            return;
        }

        Bukkit.getLogger().info("Reading Message");
        Bukkit.getLogger().info(message.getContent());

        if (message.getContent().startsWith("!")) {
            String commandName = message.getContent().toUpperCase();
            Command command = commandRegistry.get(commandName);
            if (command != null) {
                command.execute(sender, message, user);
            } else {
                Bukkit.getLogger().info("Unknown command: " + commandName);
            }
        } else {
            Bukkit.getLogger().info("Not identified as a command");
        }
    }
}

// Example command interface
interface Command {
    void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user);
}

// Example specific command implementation
class SkinChangeCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        // Implement skin change logic
    }
}

// Add more command classes (e.g., JoinCommand, FishCommand, etc.)
