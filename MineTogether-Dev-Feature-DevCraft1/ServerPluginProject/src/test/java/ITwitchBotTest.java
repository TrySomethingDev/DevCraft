import net.trysomethingdev.devcraft.twitch.ITwitchBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class ITwitchBotTest {

    private ITwitchBot bot;
    private ITwitchBot.MessageEventListener listener;

    @BeforeEach
    public void setup() {
        // Create a mock implementation of the ITwitchBot and MessageEventListener
        bot = Mockito.mock(ITwitchBot.class);
        listener = Mockito.mock(ITwitchBot.MessageEventListener.class);

        // Initialize the bot
        bot.initialize("appId", "channelName", "botUserName", "userTokenWithScopes", "refreshToken");

        // Subscribe the listener to the bot
        bot.subscribeToMessageEvent(listener);
    }

    @Test
    public void testSendMessage() {
        // Define the channel and message to send
        String channel = "testChannel";
        String message = "testMessage";

        // Send the message
        bot.sendMessage(channel, message);

        // Verify that the sendMessage method was called with the correct parameters
        verify(bot, times(1)).sendMessage(channel, message);
    }

    @Test
    public void testOnMessageReceived() {
        // Define the channel, sender, and message received
        String channel = "testChannel";
        String sender = "testSender";
        String message = "testMessage";

        // Trigger the onMessageReceived event
        listener.onMessageReceived(channel, sender, message);

        // Verify that the onMessageReceived method was called with the correct parameters
        verify(listener, times(1)).onMessageReceived(channel, sender, message);
    }
}