package net.trysomethingdev.devcraft.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class NpcBlockBreakCustomHandler implements Listener {

    private final DevCraftPlugin plugin;

    public NpcBlockBreakCustomHandler(DevCraftPlugin devCraftPlugin) {
        plugin = devCraftPlugin;
    }

    @EventHandler
    public void onNPCBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        var type = block.getType();

        var user = plugin.getUserService().getOrAddUser(event.getPlayer().getName());
        user.blockBrokenByUser(type);

    }

}
