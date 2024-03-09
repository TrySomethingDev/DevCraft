package com.gikk.twirk.types.users;

import com.gikk.twirk.types.AbstractTwitchUserFields;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;

class DefaultTwitchUserBuilder extends AbstractTwitchUserFields implements TwitchUserBuilder {

	@Override
	public TwitchUser build(TwitchMessage message) {
		parseUserProperties(message);
		return new TwitchUserImpl( this );
	}
}
