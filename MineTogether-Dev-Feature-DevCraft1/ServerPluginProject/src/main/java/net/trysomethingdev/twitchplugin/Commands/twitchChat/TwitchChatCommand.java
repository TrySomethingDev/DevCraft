package net.trysomethingdev.twitchplugin.Commands.twitchChat;



import com.gikk.twirk.Twirk;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Twirk.BotMode;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import net.trysomethingdev.twitchplugin.Utilites.Colorizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TwitchChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        DevCraftPlugin plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        TwitchBot twitchBot = plugin.getTwitchBot();
        Twirk twirk = twitchBot.getTwirk();
        StringBuilder messageBuilder = new StringBuilder();

        if (!sender.hasPermission(DevCraftPlugin.TWITCH_CHAT_PERMISSION)) return true;

        if (args.length == 0) {
            Colorizer.sendMessage(sender, "&c/tc [message]");
            return true;
        }

        if (twitchBot.getBotMode().equals(BotMode.multi)) messageBuilder.append(sender.getName()).append(": ");

        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }

        twirk.channelMessage(messageBuilder.toString());

        return true;
    }
}
