package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import org.bukkit.Bukkit;

public class RespawnCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.schedule((npcHelper, npc) -> {
            npcHelper.resetHeadPosition(npc);
            npcHelper.removeTraits(npc);

            Bukkit.broadcastMessage("Despawning" + npc.getName());
            npc.despawn();

            CommandUtil.schedule((nm, n) -> {
                Bukkit.broadcastMessage("Spawning NPC");
                npcHelper.spawnNPC(npc, plugin.getNpcGlobalSpawnPoint());
            }, user);
        }, user);
    }
}
