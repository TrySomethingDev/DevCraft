package net.trysomethingdev.twitchplugin.Commands.twitch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TwitchTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        ArrayList<String> subcommands = new ArrayList<>() {
            {
                add("newkey");
                add("status");
                add("reload");
                add("confirm");
                add("config");
            }
        };

        ArrayList<String> results = new ArrayList<>();

        //noinspection RedundantOperationOnEmptyContainer
        results.clear();

        if (args.length == 1) {
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(args[0].toLowerCase())) {
                    results.add(subcommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("newkey") | args[0].equalsIgnoreCase("reload") | args[0].equalsIgnoreCase("confirm")) {
                return List.of();
            } else if (args[0].equalsIgnoreCase("status")) {
                ArrayList<String> options = new ArrayList<>() {
                    {
                        add("enable");
                        add("disable");
                    }
                };

                for (String option : options) {
                    if (option.startsWith(args[1].toLowerCase())) {
                        results.add(option);
                    }
                }
            } else if (args[0].equalsIgnoreCase("config")) {
                ArrayList<String> options = new ArrayList<>() {
                    {
                        add("name");
                        add("channel");
                        add("oauth");
                        add("usercolor");
                        add("mode");
                    }
                };

                for (String option : options) {
                    if (option.startsWith(args[1].toLowerCase())) {
                        results.add(option);
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("newkey") | args[0].equalsIgnoreCase("reload") | args[0].equalsIgnoreCase("confirm") | args[0].equalsIgnoreCase("status")) {
                return List.of();
            } else if (args[0].equalsIgnoreCase("config")) {
                if (args[1].equalsIgnoreCase("name")) {
                    String nameTip = "The bot's account name (on Twitch)";
                    if (nameTip.startsWith(args[2].toLowerCase())) results.add(nameTip);
                } else if (args[1].equalsIgnoreCase("channel")) {
                    String channelTip = "The channel the bot should join";
                    if (channelTip.startsWith(args[2].toLowerCase())) results.add(channelTip);
                } else if (args[1].equalsIgnoreCase("oauth")) {
                    String oauthTip = "The bot's IRC oAuth token (on Twitch)";
                    if (oauthTip.startsWith(args[2].toLowerCase())) results.add(oauthTip);
                } else if (args[1].equalsIgnoreCase("usercolor")) {
                    ArrayList<String> colors = new ArrayList<>() {
                        {
                            add("&4"); add("&c"); add("&6");
                            add("&e"); add("&2"); add("&a");
                            add("&b"); add("&3"); add("&1");
                            add("&9"); add("&d"); add("&5");
                            add("&f"); add("&7"); add("&8");
                            add("&0"); add("&k"); add("&l");
                            add("&m"); add("&n"); add("&o");
                            add("&r");
                        }
                    };

                    for (String color : colors) {
                        if (color.startsWith(args[2].toLowerCase())) results.add(color);
                    }
                } else if (args[1].equalsIgnoreCase("mode")) {
                    ArrayList<String> modes = new ArrayList<>() {
                        {
                            add("single");
                            add("multi");
                        }
                    };

                    for (String mode : modes) {
                        if (mode.startsWith(args[2].toLowerCase())) results.add(mode);
                    }
                }
            }
        }

        return results;
    }
}
