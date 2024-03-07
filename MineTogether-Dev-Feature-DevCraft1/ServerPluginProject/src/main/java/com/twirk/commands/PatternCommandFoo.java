package com.twirk.commands;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

public class PatternCommandFoo extends CommandExampleBase{
	private static String PATTERN = "!foo";

	private final Twirk twirk;

	public PatternCommandFoo(Twirk twirk) {
		super(CommandType.CONTENT_COMMAND);
		this.twirk = twirk;
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
		twirk.channelMessage("bar");
		System.out.println("We are responding to a received Command FOO");
	}
}
