package net.trysomethingdev.trysomethingdevamazingplugin.handlers;

import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TorchHandler implements Listener {
    public TorchHandler(TrySomethingDevAmazingPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTorchPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
         if(block.getType() != Material.TORCH){
             return;
         }

         Bukkit.getLogger().info("A torch was placed");

         Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"), "npc create JTGaming");

         event.getBlock().setType(Material.AIR);
    }

}
