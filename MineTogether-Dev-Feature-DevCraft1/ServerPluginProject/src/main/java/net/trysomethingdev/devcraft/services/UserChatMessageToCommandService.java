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

       registerCommand("!SKIN",new SkinChangeCommand());
       registerCommand("!JOIN", new JoinCommand());
       registerCommand("!RESPAWN", new RespawnCommand());

       registerCommand("!FOLLOW", new FollowPlayerCommand());


       registerCommand("!MINE", new MineCommand());
       registerCommand("!QUARRY", new QuarryCommand());


        registerCommand("!EXIT", new ExitCommand());
       registerCommand("!STATS", new StatsCommand());

        registerCommand("!EMPTY", UnloadTrait.class);
        registerCommand("!DANCE", DanceTrait.class);
        registerCommand("!DANCE2", Dance2Trait.class);
        registerCommand("!DANCE3", Dance3Trait.class);
        registerCommand("!FISH", FishTogetherTrait.class);
        registerCommand("!WAVE", WaveTrait.class);
        registerCommand("!SPIN", SpinTrait.class);
        registerCommand("!FINDCHEST", FindChestTrait.class);
        registerCommand("!TAKEITEMFROMCHEST", TakeItemFromChestTrait.class);
    }

    private void registerCommand(String commandName, Class<? extends Trait> traitClass) {
        commandRegistry.put(commandName, new GenericCommand(commandName, traitClass));
    }

    private void registerCommand(String commandName, Command command) {
        commandRegistry.put(commandName, command);
    }

    public void processChatMessageFromSender(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        if (user == null) return;

        if (message.getContent().startsWith("!")) {
            var command = commandRegistry.get(message.getContent().toUpperCase());
            if (command != null) {
                command.execute(sender, message, user, plugin);
            } else {
                Bukkit.getLogger().info("Unknown command: " + message.getContent());
            }
        }
    }
}








