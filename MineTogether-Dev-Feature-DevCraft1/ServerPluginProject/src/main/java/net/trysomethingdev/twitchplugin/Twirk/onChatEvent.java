package net.trysomethingdev.twitchplugin.Twirk;


import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.md_5.bungee.api.ChatColor;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Utilites.Colorizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class onChatEvent implements TwirkListener {

    DevCraftPlugin plugin;

    DataManager dataManager;

    public onChatEvent() {
        plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        if (!plugin.getTwitchBot().isStatus()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getTwitchBot().getDisabledUsers().contains(player.getUniqueId().toString())) continue;

            Colorizer.sendMessage(player, ChatColor.of("#6441A5") + "<twitch> " +
                    dataManager.getConfig().getString(dataManager.USER_COLOR_PATH, "&r")+ sender.getDisplayName() + "&r: " +
                    message.getContent());
        }
    }

}
