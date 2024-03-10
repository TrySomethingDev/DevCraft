package net.trysomethingdev.twitchplugin.Twirk;


import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.md_5.bungee.api.ChatColor;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.handlers.DevCraftChatHandler;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import net.trysomethingdev.twitchplugin.Utilites.Colorizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class onChatEvent implements TwirkListener {


    DevCraftPlugin plugin;
    private final DevCraftChatHandler devCraftChatHandler;
    DataManager dataManager;

    public onChatEvent() {
        plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        this.dataManager = plugin.getDataManager();
        this.devCraftChatHandler = plugin.getDevCraftChatHandler();
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        if (!plugin.getTwitchBot().isStatus()) return;



        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getTwitchBot().getDisabledUsers().contains(player.getUniqueId().toString())) continue;

            devCraftChatHandler.handlChat(sender,message);

            Colorizer.sendMessage(player, ChatColor.of("#6441A5") + "<twitch> " +
                    dataManager.getConfig().getString(dataManager.USER_COLOR_PATH, "&r")+ sender.getDisplayName() + "&r: " +
                    message.getContent());
        }
    }

    @Override
    public void onNamesList(Collection<String> namesList ) {
        if (!plugin.getTwitchBot().isStatus()) return;
        devCraftChatHandler.handleOnNamesList(namesList);
        }


    @Override
    public void onJoin(String joinedNick) {
        if (!plugin.getTwitchBot().isStatus()) return;
        devCraftChatHandler.handleOnJoin(joinedNick);
    }

    @Override
    public void onPart(String partedNick) {
        if (!plugin.getTwitchBot().isStatus()) return;
        devCraftChatHandler.handleOnPart(partedNick);
    }


}
