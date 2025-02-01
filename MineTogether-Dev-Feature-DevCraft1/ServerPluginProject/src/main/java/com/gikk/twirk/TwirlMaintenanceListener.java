package com.gikk.twirk;

import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.mode.Mode;
import com.gikk.twirk.types.mode.Mode.MODE_EVENT;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.gikk.twirk.types.users.Userstate;

/**Class for taking care of basic tasks that our bot should do. However, writing all 
 * of these methods directly in the {@link Twirk} class would get messy. Instead, these simple
 * methods are moved to this separate class.
 * 
 * @author Gikkman
 *
 */
class TwirlMaintenanceListener implements TwirkListener{
	private final Twirk instance;
	
	TwirlMaintenanceListener(Twirk twirk) {
		this.instance = twirk;
	}
	
	@Override
	public void onAnything(String line) {
            instance.logger.debug("IN  "+line );
	}

	@Override
	public void onJoin(String joinedNick) {
		if( !instance.online.add( joinedNick ) ) {
			instance.logger.debug("\tUser " + joinedNick + " was already listed as online....");
        }
	}

	@Override
	public void onPart(String partedNick) {
		if( !instance.online.remove( partedNick ) ) {
			instance.logger.debug("\tUser " + partedNick + " was not listed as online....");
        }
	}

	
	@Override
	public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
		if (sender.isMod()) {
			instance.moderators.add(sender.getUserName());
		} else {
			instance.moderators.remove(sender.getUserName());
		}
	}

	@Override
    public void onUserstate(Userstate userstate) {
        //If the bot is a Mod, it may send 100 messages per 30 seconds
        //None-Mods may send 20 messages per 30 seconds
        if(userstate.isMod()) {
            instance.setOutputMessageDelay(30000/100);
        } else {
            instance.setOutputMessageDelay(30000/20);
        }
    }

	@Override
	public void onDisconnect() {
		instance.online.clear();
		instance.moderators.clear();
	}
	
}
