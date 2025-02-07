package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.WaveTrait;
import net.trysomethingdev.devcraft.util.CommandUtil;


public class WaveCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.resetHeadPosition(npc);
            npcHelper.removeTraits(npc);

            var trait = new WaveTrait();
            npc.addTrait(trait);
        }, user);
    }
}
