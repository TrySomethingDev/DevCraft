package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.CommandUtil;

import org.bukkit.Bukkit;


public class StatsCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((npcHelper, npc) -> {
            Bukkit.dispatchCommand(Bukkit.getPlayer(plugin.getMainPlayerUserName()), "tc Stats for player " + user.twitchUserName + ": "
                    + " Fish: " + user.fishCaught
                    + " Total Blocks Broken: " + user.blocksMined
                    + " Stone: " + user.blocksMinedStone
                    + " Iron: " + user.blocksMinedIron
                    + " Copper: " + user.blocksMinedCopper
                    + " RedStone: " + user.blocksMinedRedstone
                    + " Lapis: " + user.blocksMinedLapis
                    + " Diamond: " + user.blocksMinedDiamonds
            );
        }, user);
    }
}
