package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

public class EmptyInventory implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user) {
        user.UnloadIntoNearestChest();
    }
}
