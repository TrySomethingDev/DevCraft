package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.QuarryTrait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.Arrays;

import static net.trysomethingdev.devcraft.util.NpcHelper.*;



public class QuarryCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        StartQuarryTrait(80, 80, 400, user, plugin);

        var splitStringList = message.getContent().split(" ");
        if (Arrays.stream(splitStringList).count() != 4)
        {
            //Just do default 1x1x1
            StartQuarryTrait(10,10,400,user,plugin);
        }
        else {

            int length = 10, width = 10,depth = 400;
            try {
                length = Integer.parseInt(splitStringList[1]);
                width =  Integer.parseInt(splitStringList[2]);
                depth =  Integer.parseInt(splitStringList[3]);
            }
            catch( Exception e){

            }
            StartQuarryTrait(length,width,depth,user,plugin);
        }
    }

    private void StartQuarryTrait(int length, int width, int depth, DevCraftTwitchUser user, DevCraftPlugin plugin) {

        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }
            npcHelper.removeTraitsResetHeadPositionAndRemoveToolFromInventory(npc);

            npc.addTrait(new QuarryTrait(length,width,depth, plugin));

        }, 20 * 1);

    }
}
