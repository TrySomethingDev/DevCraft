package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

import java.nio.Buffer;

public class JoinCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {
        var npcHelper = new NpcHelper();

       // npcHelper.getOrCreateNPCAndSpawnIt(user,plugin.getNpcGlobalSpawnPoint());
        var mainPlayerLocation = Bukkit.getPlayer(plugin.getMainPlayerUserName()).getLocation();
        npcHelper.getOrCreateNPCAndSpawnIt(user,mainPlayerLocation);
    }


}
