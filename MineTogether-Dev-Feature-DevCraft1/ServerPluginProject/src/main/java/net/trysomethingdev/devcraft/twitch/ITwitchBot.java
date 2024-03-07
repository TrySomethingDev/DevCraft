package net.trysomethingdev.devcraft.twitch;


public interface ITwitchBot {

    // Method to initialize the bot
    void initialize(String appId, String channelName, String botUserName, String userTokenWithScopes, String refreshToken);

    // Method to subscribe to a message event
    void subscribeToMessageEvent(MessageEventListener listener);

    // Method to send a message
    void sendMessage(String channel, String message);

    // Inner interface for the Message Event Listener
    interface MessageEventListener {
        void onMessageReceived(String channel, String sender, String message);
    }


}
