package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.QuarryTrait;
import net.trysomethingdev.devcraft.traits.SortingTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

import java.util.Arrays;


public class SortingCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {

        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }
            npcHelper.removeTraitsResetHeadPositionAndRemoveToolFromInventory(npc);

            npc.addTrait(new SortingTrait(plugin));

        }, 20 * 1);
    }


}
