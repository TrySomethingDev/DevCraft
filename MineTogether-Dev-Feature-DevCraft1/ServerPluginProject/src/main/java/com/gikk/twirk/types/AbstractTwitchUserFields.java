package com.gikk.twirk.types;

import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.Userstate;

/**Since many types shares these typical User-fields, it is easier to have one class which does all the parsing
 * and then let the respective types Builder classes use it
 *
 * @author Gikkman
 *
 */
public abstract class AbstractTwitchUserFields {
	private static final int[] DEFAULT_COLORS = { 0xFF0000, 0x0000FF, 0x00FF00, 0xB22222, 0xFF7F50,
												  0x9ACD32, 0xFF4500, 0x2E8B57, 0xDAA520, 0xD2691E,
												  0x5F9EA0, 0x1E90FF, 0xFF69B4, 0x8A2BE2, 0x00FF7F };
	public  String[]  badges;
    public  int       bits;
    public  String    userName;
	public  String 	  displayName;
	public  int 	  color;
	public  long 	  userID;
	public  String[]  emoteSets;
    public  boolean   isOwner;
	public  boolean   isMod;
	public  boolean   isSub;
	public  boolean   isTurbo;
	public  USER_TYPE userType;
	public  Userstate userstate;
	public  String 	  rawLine;

	protected void parseUserProperties(TwitchMessage message){
		//If display-name is empty, it means that the the user name can be read from the IRC message's prefix and
		//that it has it's first character in upper case and the rest of the characters in lower case
		String channelOwner = message.getTarget().substring(1);	//Strip the # from the channel name
		TagMap r = message.getTagMap();

        // The user name is the message's prefix, between the : and the !
        String temp = message.getPrefix();
        String testLogin = r.getAsString(TwitchTags.LOGIN_NAME);
        if(testLogin.isEmpty()) {
            this.userName = temp.contains("!") ? temp.substring(1, temp.indexOf("!") ):"";
        } else {
            this.userName = testLogin;
        }

		temp =  r.getAsString(TwitchTags.DISPLAY_NAME);
		this.displayName = temp.isEmpty()
						   ? Character.toUpperCase( userName.charAt(0) ) + userName.substring(1)
						   : temp;
		temp = r.getAsString(TwitchTags.BADGES);
		this.badges = temp.isEmpty() ? new String[0] : temp.split(",");

		this.isMod   = r.getAsBoolean(TwitchTags.IS_MOD);
		this.isSub   = r.getAsBoolean(TwitchTags.IS_SUB);
		this.isTurbo = r.getAsBoolean(TwitchTags.IS_TURBO);
		this.userID = r.getAsLong(TwitchTags.USER_ID);
		this.color  = r.getAsInt(TwitchTags.COLOR);
		this.color = this.color == -1 ? getDefaultColor() : this.color;

		this.emoteSets = parseEmoteSets( r.getAsString(TwitchTags.EMOTE_SET) );
		this.userType  = parseUserType(  r.getAsString(TwitchTags.USERTYPE), displayName, channelOwner, isSub || isTurbo);

        this.isOwner = this.userType == USER_TYPE.OWNER;
		this.rawLine = message.getRaw();
	}

	private String[] parseEmoteSets(String emoteSet) {
		if( emoteSet.isEmpty() ) {
            return new String[0];
        }

		String[] sets = emoteSet.split(",");
		return sets;
	}

	private USER_TYPE parseUserType(String userType, String sender, String channelOwner, boolean isSub) {
		if( sender.equalsIgnoreCase( channelOwner ) ) {
            return USER_TYPE.OWNER;
        } else if( userType.equals( "mod" ) ) {
            return USER_TYPE.MOD;
        } else if( userType.equals( "global_mod" ) ) {
            return USER_TYPE.GLOBAL_MOD;
        } else if( userType.equals( "admin" ) ) {
            return USER_TYPE.ADMIN;
        } else if( userType.equals( "staff" ) ) {
            return USER_TYPE.STAFF;
        } else if( isSub ) {
            return USER_TYPE.SUBSCRIBER;
        } else {
            return USER_TYPE.DEFAULT;
        }
	}

	private int getDefaultColor(){
		//If display name is empty, just semi-random a color
		if( displayName.isEmpty() ) {
            return DEFAULT_COLORS[ ((int) (System.currentTimeMillis()) % DEFAULT_COLORS.length) ];
        }

		int n = displayName.charAt(0) + displayName.charAt(displayName.length() - 1);
        return DEFAULT_COLORS[n % DEFAULT_COLORS.length];
	}
}
