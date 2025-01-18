package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.DanceTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;


public class StatsCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {
        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }


               //Put code here that will get the current user and type to chat how many fish they have caught

           // Bukkit.broadcastMessage("Starting Stat Command");
          //  Bukkit.broadcastMessage("MainPlayerUserName is:" + plugin.getMainPlayerUserName());

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
         //   Bukkit.broadcastMessage("Ending Stat Command");

        }, 20);



    }
}
