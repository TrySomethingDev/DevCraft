package net.trysomethingdev.devcraft.handlers;

import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockBreakHandler implements Listener {
    private final DevCraftPlugin plugin;
    public BlockBreakHandler(DevCraftPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {



        var user = plugin.getTwitchUsersManager().getUserByTwitchUserName( event.getPlayer().getName());
        if(user != null){
            user.blockMined();
        }

    }



}
