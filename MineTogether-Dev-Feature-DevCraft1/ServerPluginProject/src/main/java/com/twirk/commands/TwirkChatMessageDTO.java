package com.twirk.commands;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

public class TwirkChatMessageDTO {


    private final String Command;
    private final TwitchUser Sender;
    private final TwitchMessage Message;

    public TwirkChatMessageDTO(String command, TwitchUser sender, TwitchMessage message) {
        this.Command = command;
        this.Sender = sender;
        this.Message = message;
    }
}
