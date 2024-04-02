package net.trysomethingdev.devcraft;

import net.trysomethingdev.devcraft.fishtogethermode.items.ItemManager;
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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.trysomethingdev.devcraft.traits.FishTogetherTrait;
import net.trysomethingdev.devcraft.handlers.*;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;


@Getter
public final class DevCraftPlugin extends JavaPlugin {
    public final static String TWITCH_SETUP_PERMISSION = "twitchchat.setup";
    public final static String TWITCH_CHAT_PERMISSION = "twitchchat.chat";
    public final static String TWITCH_TOGGLE_PERMISSION = "twitchchat.toggle";
    private DataManager dataManager;
    private EncryptionManager encryptionManager;
    private TwitchBot twitchBot;
    private DevCraftTwitchUsersManager twitchUsersManager;
    private DevCraftChatHandler devCraftChatHandler;
    public static List<String> usersToIgnoreList;
    private Location npcGlobalSpawnPoint;
    private Location fishingAreaStartPoint;
    private Location miningLocationStartPoint;

    @Override
    public void onEnable() {
        new DelayedTask(this);
        Bukkit.getLogger().info("Starting DevCraft Plugin");

        saveDefaultConfig();
        String worldName = getConfig().getString("WorldName");
        assert worldName != null;

        miningLocationStartPoint = getLocationFromConfig(worldName, "MiningLocationStartPoint");
        npcGlobalSpawnPoint =  getLocationFromConfig(worldName, "NpcGlobalSpawnPoint");
        fishingAreaStartPoint = getLocationFromConfig(worldName, "FishingAreaStartPoint");

        dataManager = new DataManager();
        encryptionManager = new EncryptionManager();
        twitchUsersManager = new DevCraftTwitchUsersManager(this, npcGlobalSpawnPoint);
        devCraftChatHandler =   new DevCraftChatHandler(this);

        InitializeChatBot();


        new FooHandler(this);
        getServer().getPluginManager().registerEvents(new NpcFishHandler(), this);
        ItemManager.init(this);
        net.trysomethingdev.devcraft.minetogethermode.items.ItemManager.init(this);
        RegisterCitizensNPCTraits();

    }
    private static void RegisterCitizensNPCTraits() {
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
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(PushTrait.class).withName("pushtrait"));
    }



    private Location getLocationFromConfig(String worldName,String locationKey) {
        double x = getConfig().getDouble(locationKey + ".X");
        double y = getConfig().getDouble(locationKey + ".Y");
        double z = getConfig().getDouble(locationKey + ".Z");
        return  new Location(Bukkit.getWorld(worldName),x,y,z);
    }

    private void InitializeChatBot() {
        twitchBot = new TwitchBot();
        boolean success = twitchBot.reload();
        if (!success) getLogger().log(Level.WARNING, "Unable to start twitch plugin fully. Please make sure it is fully configured!");
        Objects.requireNonNull(getCommand("twitch")).setExecutor(new TwitchCommand());
        Objects.requireNonNull(getCommand("twitch")).setTabCompleter(new TwitchTabCompleter());
        Objects.requireNonNull(getCommand("twitchchat")).setExecutor(new TwitchChatCommand());
        Objects.requireNonNull(getCommand("twitchchat")).setTabCompleter(new TwitchChatTabCompleter());
        Objects.requireNonNull(getCommand("twitchchaton")).setExecutor(new TwitchChatOnCommand());
        Objects.requireNonNull(getCommand("twitchchaton")).setTabCompleter(new TwitchChatOnTabCompleter());
        Objects.requireNonNull(getCommand("twitchchatoff")).setExecutor(new TwitchChatOffCommand());
        Objects.requireNonNull(getCommand("twitchchatoff")).setTabCompleter(new TwitchChatOffTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (twitchBot != null) twitchBot.getTwirk().close();
        twitchBot = null;
    }
}
