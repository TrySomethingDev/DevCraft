package net.trysomethingdev.devcraft.handlers;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.services.DevCraftTwitchUsersManager;
import net.trysomethingdev.devcraft.services.UserChatMessageToCommand;

import java.util.Collection;


public class DevCraftChatHandler {


    private final DevCraftPlugin pluggin;
    private final DevCraftTwitchUsersManager usersManager;

    private final UserChatMessageToCommand userChatMessageToCommand;

    public DevCraftChatHandler(DevCraftPlugin devCraftPlugin) {
        pluggin = devCraftPlugin;
        usersManager = pluggin.getTwitchUsersManager();
        userChatMessageToCommand = new UserChatMessageToCommand();
    }

    public void handlChat(TwitchUser sender, TwitchMessage message) {
        var user = usersManager.getOrAddUser(sender.getUserName());
        userChatMessageToCommand.processChatMessageFromSender(sender,message,user);
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
