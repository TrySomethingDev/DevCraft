package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.FollowTraitCustom;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

import static net.trysomethingdev.devcraft.util.NpcHelper.*;

public class FollowPlayerCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if(npc == null){
                if (npc == null) Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }

            //This is special logic. As we use this command to toggle following. So if you the npc already has the follow trait we just want to remove it.
            npcHelper.resetHeadPosition(npc);
                if(npc.hasTrait(FollowTraitCustom.class))
                {

                    npcHelper.removeTraits(npc);
                }
                else {
                    npcHelper.removeTraits(npc);
                    npcHelper.addFollowerTrait(npc);
                }





        }, 20);


    }
}
