package net.trysomethingdev.devcraft.services;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.citizensnpcs.api.trait.Trait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.command.*;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class UserChatMessageToCommandService {

    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final DevCraftPlugin plugin;

    public UserChatMessageToCommandService(DevCraftPlugin devCraftPlugin) {
        plugin = devCraftPlugin;

        // If you want a unique command then create a command based on BaseCommand

        // If you want to just add a new Trait you can build a trait on base trait and also register that here.

        //How should this work.
        //!HelpDev

        //!Mine  !M

        //!Fish !F


        //!Chop !C


        //!Sort !S

        //!Upgrade !U

        //!Energy  !E


       registerCommand("!SKIN",new SkinChangeCommand());
        registerCommand("!JOIN", new JoinCommand());
        registerCommand("!J", new JoinCommand());
//       registerCommand("!RESPAWN", new RespawnCommand());
//
//       registerCommand("!FOLLOW", new FollowPlayerCommand());
        registerCommand("!DEFEND", new DefendCommand());
        registerCommand("!D", new DefendCommand());
//       registerCommand("!CHOP", new ChopCommand());
        registerCommand("!MINE",  M3Trait.class);
        registerCommand("!M", M3Trait.class);
//        registerCommand("!NOTHING", new NothingCommand());
//       registerCommand("!QUARRY", new QuarryCommand());
//
//
//        registerCommand("!EXIT", new ExitCommand());
//       registerCommand("!STATS", new StatsCommand());
//
//        registerCommand("!EMPTY", UnloadTrait.class);
//        registerCommand("!DANCE", DanceTrait.class);
//        registerCommand("!DANCE2", Dance2Trait.class);
//        registerCommand("!DANCE3", Dance3Trait.class);
//        registerCommand("!FISH", FishTogetherTrait.class);
//        registerCommand("!WAVE", WaveTrait.class);
//        registerCommand("!SPIN", SpinTrait.class);
//        registerCommand("!FINDCHEST", FindChestTrait.class);
//        registerCommand("!TAKE", TakeItemFromChestTrait.class);
//        registerCommand("!SORT", new SortingCommand());
//
//        registerCommand("!THROW", ThrowTrait.class);
//        registerCommand("!GIVECAKE", GiveTrait.class);
//        registerCommand("!GIVESIGN", GiveSignTrait.class);
//        registerCommand("!FINDMINE", FindMineTrait.class);

    }

    private void registerCommand(String commandName, Class<? extends Trait> traitClass) {
        commandRegistry.put(commandName, new GenericCommand(commandName, traitClass));
    }

    private void registerCommand(String commandName, Command command) {
        commandRegistry.put(commandName, command);
    }

//    public void processChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
//        if (user == null) return;
//
//        if (message.getContent().startsWith("!")) {
//            var command = commandRegistry.get(message.getContent().toUpperCase());
//            if (command != null) {
//                command.execute(sender, message, user, plugin);
//            } else {
//                Bukkit.getLogger().info("Unknown command: " + message.getContent());
//            }
//        }
//    }

    public void processChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        if (user == null) return;

        // Only process messages starting with "!"
        if (message.getContent().startsWith("!")) {
            // Split the message by spaces to separate command and arguments
            String[] parts = message.getContent().split(" ", 2);
            String commandName = parts[0].toUpperCase(); // The command itself
            String arguments = parts.length > 1 ? parts[1] : ""; // The arguments (if any)

            // Look for the command in the registry
            var command = commandRegistry.get(commandName);
            if (command != null) {
                // Pass the arguments (if any) to the command
                command.execute(sender, message, user, plugin, arguments);
            } else {
                Bukkit.getLogger().info("Unknown command: " + commandName);
            }
        }
    }
}








