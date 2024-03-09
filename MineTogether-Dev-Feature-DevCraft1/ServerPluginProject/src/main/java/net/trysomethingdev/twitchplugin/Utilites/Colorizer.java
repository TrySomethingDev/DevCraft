package net.trysomethingdev.twitchplugin.Utilites;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Colorizer {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        //Bukkit.getLogger().info("SendMessage Commander: " + sender.getName() + " " + message.toString());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(Player sender, String message) {
       // Bukkit.getLogger().info("SendMessage Player: " + sender.getName() + " " + message.toString());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
