package net.trysomethingdev.twitchplugin.Twirk;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Encryption.EncryptionManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Level;

public class TwitchBot {

    private final DevCraftPlugin plugin;

    @Getter
    private final List<String> disabledUsers;

    private final DataManager dataManager;
    private final FileConfiguration config;

    private final EncryptionManager encryptionManager;

    @Getter @Setter
    private String name;
    @Getter @Setter
    private String channel;
    @Getter @Setter
    private String oauth;
    @Getter @Setter
    private boolean status;
    @Getter @Setter
    private BotMode botMode;

    @Getter
    private Twirk twirk;

    public TwitchBot() {
        this.plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);

        this.dataManager = plugin.getDataManager();
        this.config = dataManager.getConfig();

        this.name = config.getString(dataManager.NAME_PATH);

        this.disabledUsers = config.getStringList(dataManager.DISABLED_PATH);

        this.encryptionManager = plugin.getEncryptionManager();

        this.channel = config.getString(dataManager.CHANNEL_PATH);

        this.oauth = encryptionManager.getOAuth();

        this.status = config.getBoolean(dataManager.STATUS_PATH, true);

        this.botMode = BotMode.valueOf(config.getString(dataManager.MODE_PATH, "multi"));
    }

    public boolean reload() {
        this.channel = config.getString(dataManager.CHANNEL_PATH, null);
        this.name = config.getString(dataManager.NAME_PATH, null);
        this.oauth = encryptionManager.getOAuth();

        if (channel == null) return false;
        if (name == null) return false;
        if (oauth == null) return false;

        if (twirk != null) twirk.close();

        twirk = new TwirkBuilder(channel, name, oauth).build();

        twirk.addIrcListener(new onChatEvent());

        try {
            twirk.connect();
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to reload Twitch Bot", e);
            return false;
        }
    }

}
