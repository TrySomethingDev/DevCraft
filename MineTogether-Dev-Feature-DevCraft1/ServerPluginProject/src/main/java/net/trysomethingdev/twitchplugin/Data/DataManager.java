package net.trysomethingdev.twitchplugin.Data;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private final DevCraftPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public final String DISABLED_PATH = "TwitchIRC.disabled";
    public final String CHANNEL_PATH = "TwitchIRC.channel";
    public final String NAME_PATH = "TwitchIRC.name";
    public final String USER_COLOR_PATH = "TwitchIRC.userColor";
    public final String STATUS_PATH = "TwitchIRC.status";
    public final String MODE_PATH = "TwitchIRC.mode";

    public DataManager() {
        this.plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "config.yml");

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("config.yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null) reloadConfig();

        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null) return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {

            try {
                @SuppressWarnings("unused")
                boolean unused = configFile.getParentFile().mkdirs();
                //noinspection UnusedAssignment
                unused = configFile.createNewFile();
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
            }
        }
    }
}
