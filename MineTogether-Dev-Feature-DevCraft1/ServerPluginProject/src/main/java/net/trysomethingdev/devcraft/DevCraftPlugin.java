package net.trysomethingdev.devcraft;

import net.trysomethingdev.devcraft.services.UserChatMessageToCommandService;
import net.trysomethingdev.devcraft.services.UserService;
import net.trysomethingdev.devcraft.traits.*;
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.traits.FishTogetherTrait;
import net.trysomethingdev.devcraft.handlers.*;
import net.trysomethingdev.devcraft.util.DelayedTask;

import org.bukkit.Bukkit;

import static java.lang.Character.getType;


@Getter
public final class DevCraftPlugin extends JavaPlugin {

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
        Bukkit.getLogger().info("Starting TrySomethingDev Pluggin");

        saveDefaultConfig();
        String worldName = getConfig().getString("WorldName");
        assert worldName != null;

        miningLocationStartPoint = getLocationFromConfig(worldName, "MiningLocationStartPoint");
        npcGlobalSpawnPoint =  getLocationFromConfig(worldName, "NpcGlobalSpawnPoint");
        fishingAreaStartPoint = getLocationFromConfig(worldName, "FishingAreaStartPoint");
        mainPlayerUserName = getConfig().getString("MainPlayerUserName");


        new DelayedTask(this);

       // new NpcBlockBreakCustomHandler(this);

        dataManager = new DataManager();
        encryptionManager = new EncryptionManager();
        userService = new UserService(this);
        chatHandler =   new ChatHandler(this);
        userChatMessageToCommandService = new UserChatMessageToCommandService(this);
        twitchBot = new TwitchBot();
        boolean success = twitchBot.reload();

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

        RegisterCitizensTraits();
    }

    private Location getLocationFromConfig(String worldName,String locationKey) {
        double x = getConfig().getDouble(locationKey + ".X");
        double y = getConfig().getDouble(locationKey + ".Y");
        double z = getConfig().getDouble(locationKey + ".Z");
        return  new Location(Bukkit.getWorld(worldName),x,y,z);
    }


    private static void RegisterCitizensTraits() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FishTogetherTrait.class).withName("fishtogether"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(MinerTrait.class).withName("miner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StripMinerTrait.class).withName("stripminer"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FollowTraitCustom.class).withName("followtraitcustom"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(SkinTraitCustom.class).withName("skintraitcustom"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GoToBedTrait.class).withName("gotobed"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(LoggingTreesTrait.class).withName("loggingtrees"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(EatingTrait.class).withName("eating"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(QuarryTrait.class).withName("quarry"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DanceTrait.class).withName("dance"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(UnloadTrait.class).withName("unload"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(UnloadTrait.class).withName("wave"));
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (twitchBot != null) twitchBot.getTwirk().close();
        twitchBot = null;
    }

    public String getMainPlayerUserName() {
        return mainPlayerUserName;
    }
}
