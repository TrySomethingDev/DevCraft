package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.FishTogetherTrait;
import net.trysomethingdev.devcraft.util.CommandUtil;

public class FishingCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.removeTraits(npc);
            npc.getOrAddTrait(FishTogetherTrait.class);
        }, user);
    }
}
