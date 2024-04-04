package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;

import java.util.Arrays;

public class QuarryCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        var splitStringList = message.getContent().split(" ");
        if (Arrays.stream(splitStringList).count() != 4)
        {
            //Just do default 1x1x1
            user.QuarryCommand(80,80,400,user.plugin);
        }
        else {

            int length = 80, width = 80,depth = 400;
            try {
                length = Integer.parseInt(splitStringList[1]);
                width =  Integer.parseInt(splitStringList[2]);
                depth =  Integer.parseInt(splitStringList[3]);
            }
            catch( Exception e){

            }
            user.QuarryCommand(length,width,depth,user.plugin);
        }
    }
}
