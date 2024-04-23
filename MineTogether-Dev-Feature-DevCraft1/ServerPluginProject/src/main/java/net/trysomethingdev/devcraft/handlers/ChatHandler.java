package net.trysomethingdev.devcraft.handlers;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.services.UserService;
import net.trysomethingdev.devcraft.services.UserChatMessageToCommandService;

import java.util.Collection;


public class ChatHandler {


    private final DevCraftPlugin plugin;

    public ChatHandler(DevCraftPlugin devCraftPlugin) {
        plugin = devCraftPlugin;
    }

    public void handlChat(TwitchUser sender, TwitchMessage message) {
        var user = plugin.getUserService().getOrAddUser(sender.getUserName());
        plugin.getUserChatMessageToCommandService().processChatMessageFromSender(sender,message,user);
    }

    public void handleOnNamesList(Collection<String> namesList) {
        //Dont do anything
    }

    public  void handleOnJoin(String joinedNick) {
        //dont do anything

    }

    public  void handleOnPart(String partedNick) {

        //At this time we dont want to do anything with this.

    }
}
