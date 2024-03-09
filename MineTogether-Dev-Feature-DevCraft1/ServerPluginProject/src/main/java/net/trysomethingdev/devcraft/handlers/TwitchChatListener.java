package net.trysomethingdev.devcraft.handlers;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TwitchChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        // Do something with the message
        Bukkit.getLogger().info("WE DID IT");
        Bukkit.getLogger().info(message);
    }
}
