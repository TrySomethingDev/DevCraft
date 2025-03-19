package net.trysomethingdev.devcraft.managers;

import lombok.Getter;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.twitchconnection.TwitchOAuth;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOffCommand;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOffTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOnCommand;
import net.trysomethingdev.twitchplugin.Commands.togglecommands.TwitchChatOnTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.twitch.TwitchCommand;
import net.trysomethingdev.twitchplugin.Commands.twitch.TwitchTabCompleter;
import net.trysomethingdev.twitchplugin.Commands.twitchChat.TwitchChatCommand;
import net.trysomethingdev.twitchplugin.Commands.twitchChat.TwitchChatTabCompleter;
import net.trysomethingdev.twitchplugin.Twirk.TwitchBot;
import net.trysomethingdev.devcraft.twitchconnection.OAuthResponse;
import java.util.logging.Level;

public class TwitchBotManager {

    private final DevCraftPlugin plugin;
    @Getter
    private TwitchBot twitchBot;

    public TwitchBotManager(DevCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void initializeTwitchBot(DevCraftPlugin plugin) {
        this.plugin.getLogger().info("Initializing TwitchBot...");
        twitchBot = new TwitchBot();

        if (!attemptTwitchBotReload()) {
            handleTwitchBotFailure();
        }

        registerCommands(plugin);
    }

    private boolean attemptTwitchBotReload() {
        try {
            return twitchBot.reload();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error reloading TwitchBot", e);
            return false;
        }
    }

    private void handleTwitchBotFailure() {
        plugin.getLogger().warning("Failed to start TwitchBot, attempting refresh token...");

        String clientId = plugin.getConfig().getString("TwitchClientId");
        String clientSecret = plugin.getConfig().getString("TwitchClientSecret");
        String refreshToken = plugin.getConfig().getString("RefreshToken");

        String newTokenResponse = getNewTokenFromRefreshToken(clientId, clientSecret, refreshToken);
        if (newTokenResponse == null) {
            plugin.getLogger().warning("Refresh token failed, attempting full OAuth retrieval");
            newTokenResponse = getNewTokenAndRefreshToken(clientId, clientSecret);
        }

        twitchBot = new TwitchBot();
        twitchBot.setOauth(newTokenResponse);

        if (!attemptTwitchBotReload()) {
            plugin.getLogger().warning("Twitch plugin could not fully start. Ensure correct configuration!");
        }
    }

    private String getNewTokenFromRefreshToken(String clientId, String clientSecret, String refreshToken) {
        String newToken = TwitchOAuth.refreshOAuthToken(clientId, clientSecret, refreshToken);
        if (newToken != null) {
            plugin.getDataManager().getConfig().set("OauthToken", newToken);
            plugin.getDataManager().saveConfig();
        }
        return newToken;
    }

    private String getNewTokenAndRefreshToken(String clientId, String clientSecret) {
        OAuthResponse response = TwitchOAuth.getOAuthToken(clientId, clientSecret);
        if (response != null) {
            plugin.getDataManager().getConfig().set("RefreshToken", response.refresh_token);
            plugin.getDataManager().getConfig().set("OauthToken", response.access_token);
            plugin.getDataManager().saveConfig();
        }
        return response.access_token;
    }

    public void shutdownTwitchBot() {
        if (twitchBot != null) {
            twitchBot.getTwirk().close();
            twitchBot = null;
        }
    }

    private void registerCommands(DevCraftPlugin plugin) {

            plugin.getCommand("twitch").setExecutor(new TwitchCommand());
            plugin.getCommand("twitch").setTabCompleter(new TwitchTabCompleter());
            plugin.getCommand("twitchchat").setExecutor(new TwitchChatCommand());
            plugin.getCommand("twitchchat").setTabCompleter(new TwitchChatTabCompleter());
            plugin.getCommand("twitchchaton").setExecutor(new TwitchChatOnCommand());
            plugin.getCommand("twitchchaton").setTabCompleter(new TwitchChatOnTabCompleter());
            plugin.getCommand("twitchchatoff").setExecutor(new TwitchChatOffCommand());
            plugin.getCommand("twitchchatoff").setTabCompleter(new TwitchChatOffTabCompleter());
        }
}
