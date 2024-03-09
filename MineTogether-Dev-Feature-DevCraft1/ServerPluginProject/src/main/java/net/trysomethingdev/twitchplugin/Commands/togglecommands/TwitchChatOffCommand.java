package net.trysomethingdev.twitchplugin.Commands.togglecommands;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import net.trysomethingdev.twitchplugin.Utilites.Colorizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TwitchChatOffCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(DevCraftPlugin.TWITCH_TOGGLE_PERMISSION)) return true;

        DevCraftPlugin twitchPlugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        TwitchBot twitchBot = twitchPlugin.getTwitchBot();
        Player player = ((Player) sender);

        twitchBot.getDisabledUsers().add(player.getUniqueId().toString());
        twitchPlugin.getDataManager().getConfig().set(twitchPlugin.getDataManager().DISABLED_PATH, twitchBot.getDisabledUsers());

        Colorizer.sendMessage(sender, "&eTwitch chat is now off");

        return true;
    }
}
