package com.gikk.twirk.types.users;

import com.gikk.twirk.types.AbstractTwitchUserFields;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;

class DefaultUserstateBuilder extends AbstractTwitchUserFields implements UserstateBuilder {

	@Override
	public Userstate build(TwitchMessage message) {
		parseUserProperties(message);
		return new UserstateImpl(this);
	}
}
