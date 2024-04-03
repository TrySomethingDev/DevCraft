package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

interface Command {
    void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user);
}
