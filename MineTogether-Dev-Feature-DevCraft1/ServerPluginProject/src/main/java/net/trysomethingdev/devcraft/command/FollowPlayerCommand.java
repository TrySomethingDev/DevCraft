package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.FollowTraitCustom;
import net.trysomethingdev.devcraft.util.CommandUtil;

public class FollowPlayerCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {


        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.resetHeadPosition(npc);
            if(npc.hasTrait(FollowTraitCustom.class))
            {
                npcHelper.removeTraits(npc);
            }
            else {
                npcHelper.removeTraits(npc);
                npcHelper.addFollowerTrait(npc);
            }
        }, user);
    }
}
