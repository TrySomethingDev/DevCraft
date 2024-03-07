package net.trysomethingdev.devcraft.twitch;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import com.twirk.BotExample;
import com.twirk.SETTINGS;
import com.twirk.commands.PatternCommandExample;
import com.twirk.commands.PatternCommandExclamationPoint;
import com.twirk.commands.PrefixCommandExample;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TwitchBot implements ITwitchBot {


    public static void Start(ITwitchChatMessageListener firstListener) throws InterruptedException, IOException {
        Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        final Twirk twirk = new TwirkBuilder(SETTINGS.CHANNELNAME, SETTINGS.MY_NICK, SETTINGS.MY_PASS)
                .setVerboseMode(true)
                .build();

        //Add IRC Listeners - This includes commands.
        twirk.addIrcListener( getOnDisconnectListener(twirk) );
        twirk.addIrcListener( new PatternCommandExample(twirk));
        twirk.addIrcListener( new PatternCommandExclamationPoint(twirk,firstListener));
        twirk.addIrcListener( new PrefixCommandExample(twirk));
        Thread.sleep(2000);
        twirk.connect();	//Connect to Twitch

        //As long as we don't type .quit into the command prompt, send everything we type as a message to twitch
        String line;
        while( (line = scanner.nextLine()) != null ) {
            if(".quit".equals(line)) {
                //Close the connection to Twitch, and release all resources. This will not fire the onDisconnect
                //method
                twirk.close();
                break;
            }
            else if(".reconnect".equals(line)) {
                //Close the connection to Twitch, and release all resources. This will fire the onDisconnect method
                //however, which will cause us to reconnect to Twitch.
                twirk.disconnect();
            }
            else {
                twirk.channelMessage(line);
            }
        }
        scanner.close();	//Close the scanner
    }

    private static TwirkListener getOnDisconnectListener(final Twirk twirk) {
        return new TwirkListener() {
            @Override
            public void onDisconnect() {
                //Twitch might sometimes disconnects us from chat. If so, try to reconnect.
                try {
                    if( !twirk.connect() )
                        //Reconnecting might fail, for some reason. If so, close the connection and release resources.
                        twirk.close();
                }
                catch (IOException e) {
                    //If reconnection threw an IO exception, close the connection and release resources.
                    twirk.close();
                }
                catch (InterruptedException ignored) {  }
            }
        };
    }

    private BotExample _botExample;
    private List<ITwitchChatMessageListener> listeners = new ArrayList<>();

    @Override
    public void initialize(String appId, String channelName, String botUserName, String userTokenWithScopes, String refreshToken) {


    }

    @Override
    public void subscribeToMessageEvent(MessageEventListener listener) {

    }

    @Override
    public void sendMessage(String channel, String message) {

    }
}
