package net.trysomethingdev.devcraft;

import com.denizenscript.denizen.scripts.commands.BukkitCommandRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import net.trysomethingdev.twitchplugin.Twirk.BotMode;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import net.citizensnpcs.util.Util;


import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.commands.TutorialCommands;
import net.trysomethingdev.devcraft.denizen.FishTogetherCommand;
import net.trysomethingdev.devcraft.denizen.FishTogetherTrait;
import net.trysomethingdev.devcraft.fishtogethermode.FishTogetherModeManager;
import net.trysomethingdev.devcraft.handlers.*;
import net.trysomethingdev.devcraft.minetogethermode.MineTogetherModeManager;
import net.trysomethingdev.devcraft.util.DelayedTask;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;

import static java.lang.Character.getType;


public final class DevCraftPlugin extends JavaPlugin {


    public static java.util.logging.Logger log = java.util.logging.Logger.getLogger("Minecraft");
    /** Handle to access the Permissions plugin */
    public static Permission permissions;
    /** Name of the plugin, used in output messages */
    protected static String name = "Spawn";
    /** Path where the plugin's saved information is located */
    protected static String path = "plugins" + File.separator + name;
    /** Location of the config YML file */
    protected static String config = path + File.separator + name + ".yml";
    /** Header used for console and player output messages */
    protected static String header = "[" + name + "] ";
    /** Represents the plugin's YML configuration */
    protected static List<String> neverSpawn = new ArrayList<String>();
    protected static List<String> neverKill = new ArrayList<String>();
    protected static FileConfiguration cfg = null;
    /** True if this plugin is to be used with Permissions, false if not */
    protected boolean usePermissions = false;
    /** Limitations on how many entities can be spawned and what the maximum size of a spawned entity should be */
    protected int spawnLimit, sizeLimit;
    protected double hSpeedLimit;

    public final static String TWITCH_SETUP_PERMISSION = "twitchchat.setup";
    public final static String TWITCH_CHAT_PERMISSION = "twitchchat.chat";
    public final static String TWITCH_TOGGLE_PERMISSION = "twitchchat.toggle";

    @Getter
    private DataManager dataManager;

    @Getter
    private EncryptionManager encryptionManager;

    @Getter
    private TwitchBot twitchBot;

    @Getter
    private DevCraftTwitchUsersManager twitchUsersManager;

    @Getter
    private DevCraftChatHandler devCraftChatHandler;

    public static List<String> usersToIgnoreList;

    @Getter
    private Location npcGlobalSpawnPoint;

    @Getter
    private Location fishingAreaStartPoint;

    @Override
    public void onEnable() {

        fishingAreaStartPoint = new Location(Bukkit.getWorld("world"),-60,64,500);

        npcGlobalSpawnPoint = new Location(Bukkit.getWorld("world"),-101,64,500);
        npcGlobalSpawnPoint = Util.getCenterLocation(npcGlobalSpawnPoint.getBlock());


        Bukkit.getLogger().info("Starting TrySomethingDev Pluggin");

        LoadUsersToIgnoreList();


        new DelayedTask(this);
        dataManager = new DataManager();
        encryptionManager = new EncryptionManager();

        twitchUsersManager = new DevCraftTwitchUsersManager(this, npcGlobalSpawnPoint );
        devCraftChatHandler =   new DevCraftChatHandler(this);


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






        //  TwitchPlugin twitchChat = (TwitchPlugin) Bukkit.getPluginManager().getPlugin("TwitchPlugin");




        // AresNote: Register the command which needs com.denizenscript.denizencore.scripts.commands.AbstractCommand;
        BukkitCommandRegistry.registerCommand(FishTogetherCommand.class);

        // Plugin startup logic
        //PUT YOUR MINECRAFT USERNAME HERE
        String yourMineCraftPlayerName = "TrySomethingDev";
        //Fill in your Fishing Mode API server Base URL
        String APIBaseURL = "http://localhost:3000";

        saveDefaultConfig();
        var mineTogetherModeManager = new MineTogetherModeManager(this,yourMineCraftPlayerName);

        var fishTogetherModeManager = new FishTogetherModeManager(this,yourMineCraftPlayerName,APIBaseURL);
        //new EntityHandler(this);


        //new onChatEvent(twitchChat);

        new FooHandler(this);
       // new TorchHandler(this);
        new ChestHandler(this,mineTogetherModeManager,fishTogetherModeManager);
        new BlockBreakHandler(this,mineTogetherModeManager,fishTogetherModeManager);

        // AresNote: Registered it the old-fashioned way.
        getServer().getPluginManager().registerEvents(new NpcFishHandler(), this);

        net.trysomethingdev.devcraft.fishtogethermode.items.ItemManager.init(this);
        net.trysomethingdev.devcraft.minetogethermode.items.ItemManager.init(this);

        getCommand("givechest").setExecutor(new TutorialCommands());
        getCommand("givefishstation").setExecutor(new TutorialCommands());

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(MyTrait.class).withName("foo"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FishTogetherTrait.class).withName("fishtogether"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(MinerTrait.class).withName("miner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StripMinerTrait.class).withName("stripminer"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FollowTraitCustom.class).withName("followtraitcustom"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(SkinTraitCustom.class).withName("skintraitcustom"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GoToBedTrait.class).withName("gotobed"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TestTrait.class).withName("test"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(LoggingTreesTrait.class).withName("loggingtrees"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(EatingTrait.class).withName("eating"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(QuarryTrait.class).withName("quarry"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DanceTrait.class).withName("dance"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(UnloadTrait.class).withName("unload"));



        new DelayedTask(() -> {
            twitchUsersManager.DespawnTwitchUsersWhoHaveBeenInactiveTooLong();
        }, 20 * 10);

//
//        // Example code of trait
//        if (getServer().getPluginManager().getPlugin("Citizens") == null
//                || !getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
//            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
//        } else {
//            // Register your trait with Citizens.
//            net.citizensnpcs.api.CitizensAPI.getTraitFactory()
//                    .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(MyTrait.class).withName("mytraitname"));;
//            Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
//                @EventHandler
//                public void onCitizensEnable(CitizensEnableEvent ev) {
////                    //AresNote: Register trait with CitizensAPI
////                    CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FishTogetherTrait.class).withName("fishtogether"));
////                    //AresNote: Register trait with CitizensAPI
////                    CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(MyTrait.class).withName("helloworld"));
//
//
//                    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LynchMakesGames");
//                  //  npc.addTrait(MyTrait.class);
//                    World world2 = Bukkit.getWorld("world");
//                    npc.spawn(new Location(world2,-170,64,70));
//                    npc.addTrait(MyTrait.class);
//                }
//            }, this);
//        }
//      //  End Example code of trait

    }

    private void LoadUsersToIgnoreList() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
        Type type = new TypeToken<List<String>>(){}.getType();
        try (FileReader reader = new FileReader("DevCraftUsersToIgnore.json")) {
            List<String> strings = gson.fromJson(reader, type);

            usersToIgnoreList = strings;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {


        // Plugin shutdown logic

        if (twitchBot != null) twitchBot.getTwirk().close();
        twitchBot = null;
    }


}
