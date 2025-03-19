package net.trysomethingdev.devcraft.services;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationService {
    private final DevCraftPlugin plugin;
    private final FileConfiguration config;

    public LocationService(DevCraftPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public Location getLocation(String worldName, String locationKey) {
        double x = config.getDouble(locationKey + ".X");
        double y = config.getDouble(locationKey + ".Y");
        double z = config.getDouble(locationKey + ".Z");

        if (worldName == null || Bukkit.getWorld(worldName) == null) {
            plugin.getLogger().warning("World '" + worldName + "' not found for location: " + locationKey);
            return null;
        }

        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}


