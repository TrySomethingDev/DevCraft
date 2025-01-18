package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

public class SkinChangeCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {
        // Now you can use the arguments (e.g., "Bob")
        if (arguments.isEmpty()) {

        } else {
            // Change the skin to the provided name (e.g., "Bob")
            changeSkin(user, arguments); // Implement your skin change logic here
        }
    }

    private void changeSkin(DevCraftTwitchUser user, String skinName) {
        // Logic to change the skin
        var npcHelper = new NpcHelper();

        Bukkit.broadcastMessage("Changing Skin " + skinName);
        var splitStringList = skinName.split(" ");
        //The Skin name we are going to use is the second word.
        var skin = splitStringList[0];
        npcHelper.changeSkin(user,skin);
    }
}

