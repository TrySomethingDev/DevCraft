package net.trysomethingdev.devcraft;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.handlers.ChatHandler;
import net.trysomethingdev.devcraft.managers.TwitchBotManager;
import net.trysomethingdev.devcraft.services.DevCraftTraitRegistry;
import net.trysomethingdev.devcraft.services.LocationService;
import net.trysomethingdev.devcraft.services.UserChatMessageToCommandService;
import net.trysomethingdev.devcraft.services.UserService;
import net.trysomethingdev.devcraft.twitchconnection.OAuthResponse;
import net.trysomethingdev.devcraft.twitchconnection.TwitchOAuth;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.*;
import net.trysomethingdev.twitchplugin.Commands.twitch.*;
import net.trysomethingdev.twitchplugin.Commands.twitchChat.*;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Encryption.EncryptionManager;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public final class DevCraftPlugin extends JavaPlugin {

    @Getter
    private static DevCraftPlugin instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String PLUGIN_NAME = "DevCraft";

    public static final String TWITCH_SETUP_PERMISSION = "twitchchat.setup";
    public static final String TWITCH_CHAT_PERMISSION = "twitchchat.chat";
    public static final String TWITCH_TOGGLE_PERMISSION = "twitchchat.toggle";

    private DataManager dataManager;
    private EncryptionManager encryptionManager;
    private TwitchBotManager twitchBotManager;
    private UserService userService;
    private ChatHandler chatHandler;
    private UserChatMessageToCommandService userChatMessageToCommandService;
    private LocationService locationService;
    private DevCraftTraitRegistry devCraftTraitRegistry;
    private String worldName;
    private TwitchBot twitchBot;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("Starting " + PLUGIN_NAME + " Plugin");

        saveDefaultConfig();
        dataManager = new DataManager();
        encryptionManager = new EncryptionManager();
        userService = new UserService(this);
        chatHandler = new ChatHandler(this);
        userChatMessageToCommandService = new UserChatMessageToCommandService(this);
        locationService = new LocationService(this);
        devCraftTraitRegistry = new DevCraftTraitRegistry();
        twitchBotManager = new TwitchBotManager(this);
        twitchBot = twitchBotManager.getTwitchBot();
        
        worldName = this.getConfig().getString("WorldName");

        new DelayedTask(this);
        twitchBotManager.initializeTwitchBot(this);

        devCraftTraitRegistry.registerTraits();
    }




    @Override
    public void onDisable() {
        instance = null;
        twitchBotManager.shutdownTwitchBot();
    }

    public String getMainPlayerUserName() {
        return this.getConfig().getString("MainPlayerUserName");
    }
}
