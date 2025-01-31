package net.trysomethingdev.devcraft;

import net.citizensnpcs.api.trait.Trait;
import net.trysomethingdev.devcraft.services.UserChatMessageToCommandService;
import net.trysomethingdev.devcraft.services.UserService;
import net.trysomethingdev.devcraft.traits.*;
import net.trysomethingdev.devcraft.twitchconnection.OAuthResponse;
import net.trysomethingdev.devcraft.twitchconnection.TwitchOAuth;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOffCommand;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOffTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOnCommand;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOnTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.twitch.TwitchCommand;
import net.trysomethingdev.twitchplugin.Commands.twitch.TwitchTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.twitchChat.TwitchChatCommand;
import net.trysomethingdev.twitchplugin.Commands.twitchChat.TwitchChatTabCompleter;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Encryption.EncryptionManager;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;


import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.traits.FishTogetherTrait;
import net.trysomethingdev.devcraft.handlers.*;
import net.trysomethingdev.devcraft.util.DelayedTask;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import static java.lang.Character.getType;


@Getter
public final class DevCraftPlugin extends JavaPlugin {

    private static DevCraftPlugin instance;

    public static Logger log = Logger.getLogger("Minecraft");
    /** Name of the plugin, used in output messages */
    protected static String name = "Spawn";
    public final static String TWITCH_SETUP_PERMISSION = "twitchchat.setup";
    public final static String TWITCH_CHAT_PERMISSION = "twitchchat.chat";
    public final static String TWITCH_TOGGLE_PERMISSION = "twitchchat.toggle";
    private DataManager dataManager;
    private EncryptionManager encryptionManager;
    private TwitchBot twitchBot;
    private UserService userService;
    private ChatHandler chatHandler;
    private UserChatMessageToCommandService userChatMessageToCommandService;
    private Location npcGlobalSpawnPoint;
    private Location fishingAreaStartPoint;
    private Location miningLocationStartPoint;
    private String mainPlayerUserName;

    @Override
    public void onEnable() {

        instance = this; // Save the instance

        Bukkit.getLogger().info("Starting TrySomethingDev Pluggin");



        saveDefaultConfig();
        String worldName = getConfig().getString("WorldName");
        //assert worldName != null;

   //     getServer().getPluginManager().registerEvents(new ExperimentalHandler(this), this);

        miningLocationStartPoint = getLocationFromConfig(worldName, "MiningLocationStartPoint");
        npcGlobalSpawnPoint =  getLocationFromConfig(worldName, "NpcGlobalSpawnPoint");
        fishingAreaStartPoint = getLocationFromConfig(worldName, "FishingAreaStartPoint");
        mainPlayerUserName = getConfig().getString("MainPlayerUserName");
        var refreshToken = getConfig().getString("RefreshToken");

        var clientId = getConfig().getString("TwitchClientId");
        var clientSecret = getConfig().getString("TwitchClientSecret");




        new DelayedTask(this);

       // new NpcBlockBreakCustomHandler(this);

        dataManager = new DataManager();
        encryptionManager = new EncryptionManager();
        userService = new UserService(this);
        chatHandler =   new ChatHandler(this);
        userChatMessageToCommandService = new UserChatMessageToCommandService(this);

        getLogger().info("Creating new TwitchBot!!!!!!!!!!!!!!");
      // var foo = GetNewTokenAndRefreshToken(clientId,clientSecret);
        twitchBot = new TwitchBot();

        boolean success = false;
        try{
            success  = twitchBot.reload();
        }
        catch(Exception e) {
            //  Block of code to handle errors

        }


        if(!success){
            getLogger().log(Level.WARNING,"Failed one time to load twitch bot");
            getLogger().info("Attempting to use Refresh token");

            String newTokenResponse = getNewTokenFromRefreshToken(clientId, clientSecret, refreshToken);
            getLogger().info("Oauth Response when using refresh token is new token");
            getLogger().info(newTokenResponse);

            if(newTokenResponse == null)
            {
                getLogger().log(Level.WARNING,"Failed a second time to load twitch bot");
               newTokenResponse = GetNewTokenAndRefreshToken(clientId,clientSecret);

            }
            twitchBot = new TwitchBot();
            twitchBot.setOauth(newTokenResponse);

            try{
                getLogger().info("Success: Reloading Twitch Bot");
                success = twitchBot.reload();
            }
            catch(Exception e) {
                //  Block of code to handle errors

            }



//            if(!success)
//            {
//                getLogger().log(Level.WARNING,"Failed a second time to load twitch bot");
//                GetNewTokenAndRefreshToken(clientId,clientSecret);
//                success = twitchBot.reload();
//                if(!success)
//                {
//                    getLogger().warning("I dont know what to do here");
//                }
//
//
//            }
        }

        if (!success) getLogger().log(Level.WARNING, "Unable to start twitch plugin fully. Please make sure it is fully configured!");
        getCommand("twitch").setExecutor(new TwitchCommand());
        getCommand("twitch").setTabCompleter(new TwitchTabCompleter());
        getCommand("twitchchat").setExecutor(new TwitchChatCommand());
        getCommand("twitchchat").setTabCompleter(new TwitchChatTabCompleter());
        getCommand("twitchchaton").setExecutor(new TwitchChatOnCommand());
        getCommand("twitchchaton").setTabCompleter(new TwitchChatOnTabCompleter());
        getCommand("twitchchatoff").setExecutor(new TwitchChatOffCommand());
        getCommand("twitchchatoff").setTabCompleter(new TwitchChatOffTabCompleter());

        new ExperimentalHandler(this);
        getServer().getPluginManager().registerEvents(new NpcFishHandler(), this);

        registerCitizensTraits();
    }

    @Nullable
    private String getNewTokenFromRefreshToken(String clientId, String clientSecret, String refreshToken) {



        String newTokenResponse = TwitchOAuth.refreshOAuthToken(clientId, clientSecret, refreshToken);
        if(newTokenResponse != null){
            String OAUTH_PATH = "OauthToken";
            var config = this.getDataManager().getConfig();

            config.set(OAUTH_PATH, newTokenResponse);
        }



        return newTokenResponse;
    }

    private String GetNewTokenAndRefreshToken(String clientId, String clientSecret) {
        //Get Twitch Connection Handled
        OAuthResponse response = null;

             response = TwitchOAuth.getOAuthToken(clientId,clientSecret);
             getLogger().info(response.access_token);
             getLogger().info(response.refresh_token);
             String OAUTH_PATH = "OauthToken";
             String Refresh_Path = "RefreshToken";
            var config = this.getDataManager().getConfig();
            config.set(Refresh_Path,response.refresh_token);
            config.set(OAUTH_PATH, response.access_token);

            this.getDataManager().saveConfig();







        this.getLogger().info(response.token_type);
        this.getLogger().info(response.access_token);
        this.getLogger().info(response.refresh_token);
        this.getLogger().info(String.valueOf(response.expires_in));
        this.getLogger().info(Arrays.toString(response.scope));

     return response.access_token;
        //End Twitch Connection Section
    }

    private Location getLocationFromConfig(String worldName,String locationKey) {
        double x = getConfig().getDouble(locationKey + ".X");
        double y = getConfig().getDouble(locationKey + ".Y");
        double z = getConfig().getDouble(locationKey + ".Z");
        return  new Location(Bukkit.getWorld(worldName),x,y,z);
    }

    private void registerCitizensTraits() {
        Reflections reflections = new Reflections("net.trysomethingdev.devcraft.traits");
        Set<Class<? extends Trait>> traitClasses = reflections.getSubTypesOf(Trait.class);

        for (Class<? extends Trait> traitClass : traitClasses) {
            String traitName = traitClass.getSimpleName().toLowerCase();
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(traitClass).withName(traitName));
            log.info("Registered trait: " + traitName);
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null; // Clear the instance
        if (twitchBot != null) twitchBot.getTwirk().close();
        twitchBot = null;
    }
    public static DevCraftPlugin getInstance() {
        return instance;
    }
    public String getMainPlayerUserName() {
        return mainPlayerUserName;
    }
}
