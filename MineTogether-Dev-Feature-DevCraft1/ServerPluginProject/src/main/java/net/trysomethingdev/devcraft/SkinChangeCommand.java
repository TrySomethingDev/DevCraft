package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.Command;

public class SkinChangeCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        var splitStringList = message.getContent().split(" ");
        //The Skin name we are going to use is the second word.
        var skin = splitStringList[1];
        user.changeSkin(skin);
    }
}

