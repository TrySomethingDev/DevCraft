package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.CommandUtil;

public class JoinCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.getOrCreateNPCAndSpawnIt(user,plugin.getNpcGlobalSpawnPoint());
        }, user);
    }
}
