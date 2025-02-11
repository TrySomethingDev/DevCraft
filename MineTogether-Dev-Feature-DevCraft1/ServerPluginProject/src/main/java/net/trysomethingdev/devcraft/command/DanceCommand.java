package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.DanceTrait;
import net.trysomethingdev.devcraft.util.CommandUtil;


public class DanceCommand implements Command {

    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((helper, npc) -> {
                helper.resetHeadPosition(npc);
                helper.removeTraits(npc);

                var dance = new DanceTrait();
                npc.addTrait(dance);
            }, 
            user, 
            20
        );
    }
}
