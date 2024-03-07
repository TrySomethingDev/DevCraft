package net.trysomethingdev.devcraft;

import com.denizenscript.denizen.scripts.commands.BukkitCommandRegistry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.commands.TutorialCommands;
import net.trysomethingdev.devcraft.denizen.FishTogetherCommand;
import net.trysomethingdev.devcraft.denizen.FishTogetherTrait;
import net.trysomethingdev.devcraft.fishtogethermode.FishTogetherModeManager;
import net.trysomethingdev.devcraft.handlers.*;
import net.trysomethingdev.devcraft.minetogethermode.MineTogetherModeManager;
import net.trysomethingdev.devcraft.traits.MinerTrait;
import net.trysomethingdev.devcraft.traits.MyTrait;
import net.trysomethingdev.devcraft.traits.StripMinerTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class TrySomethingDevAmazingPlugin extends JavaPlugin {


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


    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Starting TrySomethingDev Pluggin");



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
        new DelayedTask(this);

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
////

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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
