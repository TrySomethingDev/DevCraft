package com.twirk.commands;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.trysomethingdev.devcraft.twitch.ITwitchChatMessageListener;
import net.trysomethingdev.devcraft.twitch.TwitchChatMessageAddedEvent;

import java.util.ArrayList;
import java.util.List;

public class PatternCommandExclamationPoint extends CommandExampleBase{

	public PatternCommandExclamationPoint(Twirk twirk,ITwitchChatMessageListener firstListener) {
		super(CommandType.CONTENT_COMMAND);
		subscribeToOnMassageReceivedEvent(firstListener);
		this.twirk = twirk;
	}
	private static String PATTERN = "!";

	private final Twirk twirk;

	private List<ITwitchChatMessageListener> listeners = new ArrayList<>();


	private void fireTwitchChatMessageAddedEvent(TwitchChatMessageAddedEvent event) {
		for (var listener : listeners) {
			listener.onTwitchChatMessageAdded(event);
		}
	}


	//AddFishingStationManagerListener
	public void subscribeToOnMassageReceivedEvent(ITwitchChatMessageListener listener)
	{
		listeners.add(listener);
	}




	
	@Override
	protected String getCommandWords() {
		return PATTERN;
	}

	@Override
	protected USER_TYPE getMinUserPrevilidge() {
		return USER_TYPE.DEFAULT;
	}

	@Override
	protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
		var twirkChatMessageDTO = new TwirkChatMessageDTO(command,sender,message);
		fireTwitchChatMessageAddedEvent(new TwitchChatMessageAddedEvent(twirkChatMessageDTO));

	}
}

