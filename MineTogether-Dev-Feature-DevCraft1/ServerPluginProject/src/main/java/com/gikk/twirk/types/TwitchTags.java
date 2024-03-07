package com.gikk.twirk.types;

/**This is a comprehensive list of all <code>TAG</code>  identifiers that a message from Twitch can have.
 *
 * @author Gikkman
 *
 */
public class TwitchTags {

	/**Denotes the message's unique ID
	 */
	public static final String ID			= "id";

	/**Denotes the user's unique ID
	 */
	public static final String USER_ID 		= "user-id";

	/**The users display name (on Twitch). Escaped like the following:<br><br><table style="width:500"><tr>
	 *<td><b>CHARACTER		</b></td><td><b>ESCAPED BY</b></td><tr>
	 *<td>; (semicolon)	</td><td>\: (backslash and colon)</td><tr>
	 *<td>SPACE			</td><td>\s </td><tr>
	 *<td>\				</td><td>\\ </td><tr>
	 *<td>CR			</td><td>\r </td><tr>
	 *<td>LF			</td><td>\n </td><tr>
	 *<td>all others	</td><td>the character itself</td></tr></table>
	 */
	public static final String DISPLAY_NAME	= "display-name";

	/**Denotes badges this message has. Can be:<br>
	 * {@code staff, admin, global_mod, moderator, subscriber, turbo } and/or {@code bits}
	 */
	public static final String BADGES 		= "badges";

    /**Denotes the number of bits that was cheered in this message
     */
    public static final String BITS         = "bits";

	/**Denotes the users color. This comes as a hexadecimal value. Can be empty if never set by the user
	 */
	public static final String COLOR 		= "color";

	/**Denotes if the user is a subscriber or not. <code>1</code> for <code>true</code>, <code>0</code> for <code>false</code>
	 */
	public static final String IS_SUB 		= "subscriber";

	/**Denotes if the user is a moderator or not. <code>1</code> for <code>true</code>, <code>0</code> for <code>false</code>
	 */
	public static final String IS_MOD 		= "mod";

	/**Denotes if the user has turbo or not. <code>1</code> for <code>true</code>, <code>0</code> for <code>false</code>
	 */
	public static final String IS_TURBO 	= "turbo";

	/**Is either <code>empty, mod, global_mod, admin</code> or <code>staff</code>.
	*  <li>The broadcaster can have any of these, including empty.
	*/
	public static final String USERTYPE 	= "user-type";

	/**Contains information to replace text in the message with the emote images and <b>can be empty</b>. The format is as follows:<br><br>
	 * <code>emote_id:first_index-last_index,another_first-another_last/another_emote_id:first_index-last_index</code><br><br>
	 *
	 * <code>emote_id</code> is the number to use in this URL: <code>http://static-cdn.jtvnw.net/emoticons/v1/:emote_id/:size</code> (<i>size is 1.0, 2.0 or 3.0</i>)<br><br>
	 *
	 * Emote indexes are simply character indexes. <code>ACTION</code> does not count and indexing starts from the first character that is part of the user's "actual message".<br>
	 * Both start and end is inclusive.  If the message is "Kappa" (emote id 25), start is from character 0 (K) to character 4 (a).
	 */
	public static final String EMOTES		 = "emotes";

	/** Contains your emote set, which you can use to request a subset of <a href="https://github.com/justintv/Twitch-API/blob/master/v3_resources/chat.md#get-chatemoticon_images">/chat/emoticon_images</a>
	 * <li>Always contains at least {@code 0}
	 * <li>Ex: <code>https://api.twitch.tv/kraken/chat/emoticon_images?emotesets=0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239</code>
	 */
	public static final String EMOTE_SET	= "emote-sets";

	/** The user's login name. Can be different from the user's display name
	 */
	public final static String LOGIN_NAME 	= "login";

	/** Identifies what kind of notice it was.
	 */
	public final static String MESSAGE_ID 	= "msg-id";

	/** The message printed in chat along with this notice.
	 */
	public final static String SYSTEM_MESSAGE = "system-msg";

	/** The number of cumulative months the user has subscribed for in a resub notice.
	 */
	public final static String PARAM_MONTHS = "msg-param-cumulative-months";

	/** The number of consecutive months the user has subscribed for in a resub notice.
	 */
	public final static String PARAM_STREAK = "msg-param-streak-months";

	/** Denotes if the user share the streak or not. <code>1</code> for <code>true</code>, <code>0</code> for <code>false</code>
	 */
	public final static String PARAM_SHARE_STREAK = "msg-param-should-share-streak";

    /** The type of subscription plan being used.
     * Valid values: Prime, 1000, 2000, 3000. 1000, 2000, and 3000 refer to the
     * first, second, and third levels of paid subscriptions,
     * respectively (currently $4.99, $9.99, and $24.99).
     */
    public final static String PARAM_SUB_PLAN = "msg-param-sub-plan";

    /** The display name of the subscription plan. This may be a default name or
     * one created by the channel owner.
     */
    public static String PARAM_SUB_PLAN_NAME = "msg-param-sub-plan-name";

	/** Denotes the room's ID which the message what sent to. Each room's ID is unique
	 */
	public static final String ROOM_ID 		= "room-id";

	/** <code>broadcaster-lang</code> is the chat language when <a href="https://blog.twitch.tv/your-channel-your-chat-your-language-80306ab98a59#.i4rpk1gn1">broadcaster language mode</a> is enabled, and empty otherwise. <br>
	 * A few examples would be en for English, fi for Finnish and es-MX for Mexican variant of Spanish
	 */
	public static final String ROOM_LANG 	= "broadcaster-lang";

	/** Messages with more than 9 characters must be unique. 0 means disabled, 1 enabled
	 */
	public static final String R9K_ROOM 	= "r9k";

	/** Only subscribers and moderators can chat. 0 disabled, 1 enabled.
	 */
	public static final String SUB_ONLY_ROOM= "subs-only";

	/** Only followers can chat. -1 disabled, 0 all followers can chat, > 0 only users following for at least the specified number of minutes can chat.
	 */
	public static final String FOLLOWERS_ONLY_ROOM = "followers-only";

	/** Emote-only mode. If enabled, only emotes are allowed in chat. Valid values: 0 (disabled) or 1 (enabled).
	 */
	public static final String EMOTE_ONLY_ROOM = "emote-only";

	/** Determines how many seconds chatters without moderator privileges must wait between sending messages
	 */
	public static final String SLOW_DURATION= "slow";

	/** The duration of the timeout in seconds. The tag is omitted on permanent bans.
	 */
	public static final String BAN_DURATION = "ban-duration";

	/** The reason the moderator gave for the timeout or ban.
	 */
	public static final String BAN_REASON   = "ban-reason";

    /** The UNIX timestamp a message reached the Twitch server
     */
    public static final String TMI_SENT_TS = "tmi-sent-ts";

    /** The number of viewers which participates in a raid
     */
    public static final String PARAM_VIEWER_COUNT = "msg-param-viewerCount";

    /** The login name of the user initiating a raid
     */
    public static final String PARAM_LOGIN_NAME = "msg-param-login";

    /** The display name of a user initiating a raid
     */
    public static final String PARAM_DISPLAY_NAME = "msg-param-displayName";

    /** The name of the ritual this notice is for. Valid value: new_chatter
     */
    public static String PARAM_RITUAL_NAME = "msg-param-ritual-name";

    /** The display name of the subscriber gift receiver.
     */
    public static String PARAM_RECIPIANT_NAME = "msg-param-recipient-name";

    /** The display name of the subscriber gift receiver.
     */
    public static String PARAM_RECIPIANT_DISPLAY_NAME = "msg-param-recipient-display-name";

    /** The user ID of the subscriber gift receiver.
     */
    public static String PARAM_RECIPIANT_ID = "msg-param-recipient-id";
}
