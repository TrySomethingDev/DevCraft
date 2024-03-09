package com.gikk.twirk.types.roomstate;

import com.gikk.twirk.types.AbstractType;

/**
 * Class for representing a ROOMSTATE from Twitch.<br><br>
 * <p>
 * A ROOMSTATE means that there were some changes to the rooms attributes. When you first join a room, you will get a
 * snapshot of the rooms current ROOMSTATE. If changes occur, only the relevant changes will be included in the
 * ROOMSTATE A room has four different attributes <ul>
 * <li>broadcasterLanguage - The chat language when broadcaster language mode is enabled, and empty otherwise.
 * <li>r9k - R9K mode means that messages longer than 9 characters must be unique. Useful for supressing copypastas and
 * avoid spamming long messages
 * <li>SubMode - Only subs and mods can write.
 * <li>SlowMode - Only mods can post frequent, all others must wait a designated time between posting messages.
 * <li>FollowerMode - If enabled, controls which followers can chat. Valid values: -1 (disabled),
 * 0 (all followers can chat), or a non-negative integer (only users following for at least the specified number of
 * minutes can chat).
 * <li>Emote-only mode - Only emotes are allowed in chat.
 *
 * @author Gikkman
 */
public interface Roomstate extends AbstractType {

  /**
   * The chat language when broadcaster language mode is enabled, and empty otherwise. Might be empty, if this ROOMSTATE
   * had no information about it
   *
   * @return This ROOMSTATE's broadcasterLanguage (might be empty)
   */
  String getBroadcasterLanguage();

  /**
   * If 1, it means that R9K mode is enabled. 0 means it is disabled. -1 means that this ROOMSTATE did not mention r9k
   *
   * @return r9k mode status of this ROOMSTATE
   */
  int get9kMode();

  /**
   * If 1, it means that sub mode is enabled. 0 means it is disabled. -1 means that this ROOMSTATE did not mention
   * subMode
   *
   * @return sub mode status of this ROOMSTATE
   */
  int getSubMode();

  /**
   * If -1, it means that followers mode is disabled. 0 means it is enabled. If greater than 0, it means only users
   * following for at least the specified number of minutes can chat.
   *
   * @return followers mode status of this ROOMSTATE
   */
  int getFollowersMode();


  /**
   * If 1, it means that emote-only mode is enabled. 0 means it is disabled. -1 means that this ROOMSTATE did not
   * mention emote-only mode
   *
   * @return emote-only mode status of this ROOMSTATE
   */
  int getEmoteOnlyMode();

  /**
   * If greater than 0, it means that users must wait more than X seconds between posting messages. -1 means that this
   * ROOMSTATE did not mention slowMode
   *
   * @return slow mode status of this ROOMSTATE
   */
  int getSlowModeTimer();

}
