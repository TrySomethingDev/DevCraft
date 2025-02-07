package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import org.bukkit.Bukkit;

public class SkinChangeCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin) {
        CommandUtil.run((npcHelper, npc) -> {
            Bukkit.broadcastMessage("Changing Skin " + message);
            var splitStringList = message.getContent().split(" ");
            //The Skin name we are going to use is the second word.
            var skin = splitStringList[1];
            npcHelper.changeSkin(user,skin);
        }, user);
    }
}

