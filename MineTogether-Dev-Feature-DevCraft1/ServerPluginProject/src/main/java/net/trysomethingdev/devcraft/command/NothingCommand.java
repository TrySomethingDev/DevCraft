package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.services.NPCState;
import net.trysomethingdev.devcraft.traits.NPCBehaviorTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NothingCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {

        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);

            NPCBehaviorTrait behavior = npc.getOrAddTrait(NPCBehaviorTrait.class);

            behavior.setState(NPCState.NOTHING);
        }, 20);

    }


}
