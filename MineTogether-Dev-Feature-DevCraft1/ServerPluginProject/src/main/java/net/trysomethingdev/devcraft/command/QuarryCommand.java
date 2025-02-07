package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.QuarryTrait;
import java.util.Arrays;




public class QuarryCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {


        StartQuarryTrait(80, 80, 400, user, plugin);


        var splitStringList = message.getContent().split(" ");
        if (Arrays.stream(splitStringList).count() != 4)
        {
            //Just do default 1x1x1
            StartQuarryTrait(10,10,400,user,plugin);
        }
        else {

            int length = 10, width = 10,depth = 400;
            try {
                length = Integer.parseInt(splitStringList[1]);
                width =  Integer.parseInt(splitStringList[2]);
                depth =  Integer.parseInt(splitStringList[3]);
            }
            catch( Exception e){

            }
            StartQuarryTrait(length,width,depth,user,plugin);
        }
    }

    private void StartQuarryTrait(int length, int width, int depth, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        CommandUtil.schedule((npcHelper, npc) -> {
            if(npc.hasTrait(QuarryTrait.class)) npc.removeTrait(QuarryTrait.class);
            npcHelper.resetHeadPosition(npc);
            npcHelper.removeTraits(npc);
            var quarry = new QuarryTrait(length,width,depth, plugin);
            npc.addTrait(quarry);
        }, user, 20 * 1);
    }
}
