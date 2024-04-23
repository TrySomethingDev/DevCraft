package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

public class RespawnCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }

            npcHelper.resetHeadPosition(npc);
            npcHelper.removeTraits(npc);

            Bukkit.broadcastMessage("Despawning" + npc.getName());
            npc.despawn();

            //Respawn
            new DelayedTask(() -> {
                var npcHelper2 = new NpcHelper();
                if (npc != null) {
                    Bukkit.broadcastMessage("Spawning NPC");
                    npcHelper2.spawnNPC(npc, plugin.getNpcGlobalSpawnPoint());
                }
            }, 20);

        }, 20);
    }




}
