package net.trysomethingdev.twitchplugin.Commands.twitch;



import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Encryption.EncryptionManager;
import net.trysomethingdev.twitchplugin.Twirk.BotMode;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import net.trysomethingdev.twitchplugin.Utilites.Colorizer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class TwitchCommand implements CommandExecutor {


    HashMap<String, String> waiting = new HashMap<>();

    HashMap<String, String> oauths = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!sender.hasPermission(DevCraftPlugin.TWITCH_SETUP_PERMISSION)) {
            Colorizer.sendMessage(sender, "&cYou do not have permission to use this command");
            return true;
        }

        if (args.length == 0) return true;

        DevCraftPlugin plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);

        TwitchBot twitchBot = plugin.getTwitchBot();

        DataManager dataManager = plugin.getDataManager();

        FileConfiguration config = dataManager.getConfig();

        EncryptionManager encryptionManager = plugin.getEncryptionManager();

        switch(args[0].toLowerCase()) {
            case "newkey" -> {
                encryptionManager.generateNewKey();
                twitchBot.reload();
                Colorizer.sendMessage(sender, "&aCreated new key");
            }
            case "reload" -> {
                plugin.getTwitchBot().reload();
                Colorizer.sendMessage(sender, "&aReloaded Twitch Bot");
            }
            case "status" -> {
                if (args.length == 1) {
                    if (twitchBot.isStatus()) Colorizer.sendMessage(sender, "&aTwitch plugin is enabled");
                    else Colorizer.sendMessage(sender, "&cTwitch plugin is disabled");
                } else if (args[1].equalsIgnoreCase("enable")) {
                    config.set(dataManager.STATUS_PATH, true);
                    dataManager.saveConfig();
                    twitchBot.setStatus(true);
                    Colorizer.sendMessage(sender, "&aTwitch plugin now enabled!");
                } else if (args[1].equalsIgnoreCase("disable")) {
                    config.set(dataManager.STATUS_PATH, false);
                    dataManager.saveConfig();
                    twitchBot.setStatus(false);
                    Colorizer.sendMessage(sender, "&cTwitch plugin now disabled!");
                } else {
                    Colorizer.sendMessage(sender, "&c/twitch status [enable|disable]");
                }
            }
            case "config" -> {
                if (args.length == 1) {
                    Colorizer.sendMessage(sender, "&c/twitch config [name|channel|oauth|usercolor|mode] {newValue}");
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("name")) {
                        Colorizer.sendMessage(sender, "&eThe current bot name is &3" + twitchBot.getName());
                    } else if (args[1].equalsIgnoreCase("channel")) {
                        Colorizer.sendMessage(sender, "&eThe current channel the bot connects to is &3" + twitchBot.getChannel());
                    } else if (args[1].equalsIgnoreCase("oauth")) {
                        Colorizer.sendMessage(sender, "&cWARNING THIS WILL SHOW YOUR OAUTH. TYPE /twitch confirm TO SEE. DO NOT DO THIS ON STREAM!");
                        Colorizer.sendMessage(sender, "&cYou have &630 &cseconds to confirm the command");
                        waiting.put(sender.getName(), "viewOAuth");
                        Bukkit.getScheduler().runTaskLater(plugin, () -> waiting.remove(sender.getName()), 600L);
                    } else if (args[1].equalsIgnoreCase("usercolor")) {
                        Colorizer.sendMessage(sender, "&eThe current color for twitch users is " + config.getString(dataManager.USER_COLOR_PATH, "&r").replace('&', '*'));
                    } else if (args[1].equalsIgnoreCase("mode")) {
                        Colorizer.sendMessage(sender, "&eThe current mode is &3" + twitchBot.getBotMode());
                    } else {
                        Colorizer.sendMessage(sender, "&c/twitch config [name|channel|oauth|usercolor|mode] {newValue}");
                    }
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("name")) {
                        config.set(dataManager.NAME_PATH, args[2]);
                        twitchBot.setName(args[2]);
                        twitchBot.reload();
                        Colorizer.sendMessage(sender, "&aSuccessfully changed plugin name");
                    } else if (args[1].equalsIgnoreCase("channel")) {
                        config.set(dataManager.CHANNEL_PATH, "#"+args[2]);
                        twitchBot.setChannel("#"+args[2]);
                        twitchBot.reload();
                        Colorizer.sendMessage(sender, "&aSuccessfully changed plugin channel");
                    } else if (args[1].equalsIgnoreCase("oauth")) {
                        Colorizer.sendMessage(sender, "&cConfirm changing the oauth with /twitch confirm");
                        Colorizer.sendMessage(sender, "&c you have &630 &cseconds to confirm");
                        waiting.put(sender.getName(), "changeOAuth");
                        oauths.put(sender.getName(), args[2]);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            waiting.remove(sender.getName());
                            oauths.remove(sender.getName());
                        }, 600L);
                    } else if (args[1].equalsIgnoreCase("usercolor")) {
                        config.set(dataManager.USER_COLOR_PATH, args[2]);
                        Colorizer.sendMessage(sender, "&aSuccessfully changed the twitch user color");
                    } else if (args[1].equalsIgnoreCase("mode")) {
                        if (args[2].equalsIgnoreCase("single") | args[2].equalsIgnoreCase("multi")) {
                            twitchBot.setBotMode(BotMode.valueOf(args[2].toLowerCase()));
                            config.set(dataManager.MODE_PATH, BotMode.valueOf(args[2].toLowerCase()).toString());
                            Colorizer.sendMessage(sender, "&aSuccessfully changed the plugin mode");
                        } else {
                            Colorizer.sendMessage(sender, "&c/twitch config mode [single|multi]");
                        }
                    }
                    dataManager.saveConfig();
                } else {
                    Colorizer.sendMessage(sender, "&c/twitch config [name|channel|oauth|usercolor|mode] {newValue}");
                }
            }
            case "confirm" -> {
                if (!waiting.containsKey(sender.getName())) {
                    Colorizer.sendMessage(sender, "&cYou are not waiting to confirm any commands");
                    return true;
                }

                if (waiting.get(sender.getName()).equals("viewOAuth")) {
                    Colorizer.sendMessage(sender, "&eThe current oauth is &3" + twitchBot.getOauth());
                    waiting.remove(sender.getName());
                } else if (waiting.get(sender.getName()).equalsIgnoreCase("changeOAuth")) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        String newOAuth = oauths.get(sender.getName());
                        ArrayList<Character> key = new ArrayList<>();
                        for (char c : encryptionManager.getKey().toCharArray()) {
                            key.add(c);
                        }
                        config.set(encryptionManager.OAUTH_PATH, newOAuth);
                        dataManager.saveConfig();
                        config.set(encryptionManager.OAUTH_PATH, encryptionManager.encrypt(newOAuth, key));
                        dataManager.saveConfig();
                        twitchBot.setOauth(newOAuth);
                        twitchBot.reload();
                        Colorizer.sendMessage(sender, "&aSuccessfully changed oauth");
                        waiting.remove(sender.getName());
                        oauths.remove(sender.getName());
                    });
                }
            }
        }

        return true;
    }
}
