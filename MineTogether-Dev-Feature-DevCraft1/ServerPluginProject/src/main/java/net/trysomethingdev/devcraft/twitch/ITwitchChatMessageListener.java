package net.trysomethingdev.devcraft.twitch;

import java.util.EventListener;

public interface ITwitchChatMessageListener extends EventListener {
    void onTwitchChatMessageAdded(TwitchChatMessageAddedEvent event);
}


