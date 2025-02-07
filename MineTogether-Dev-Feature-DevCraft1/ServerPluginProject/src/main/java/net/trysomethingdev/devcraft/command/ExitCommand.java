package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;

public class ExitCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.resetHeadPosition(npc);
            npcHelper.removeTraits(npc);
            if(npc.isSpawned()) npc.despawn();
        }, user);
    }
}
