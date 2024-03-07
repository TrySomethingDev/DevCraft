package net.trysomethingdev.devcraft.handlers;

import net.trysomethingdev.devcraft.TrySomethingDevAmazingPlugin;
import net.trysomethingdev.devcraft.fishtogethermode.FishTogetherModeManager;
import net.trysomethingdev.devcraft.minetogethermode.MineTogetherModeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakHandler implements Listener{

    private MineTogetherModeManager _mineTogetherModeManager;
    private FishTogetherModeManager _fishTogetherModeManager;
        public BlockBreakHandler(TrySomethingDevAmazingPlugin plugin, MineTogetherModeManager mineTogetherModeManager, FishTogetherModeManager fishTogetherModeManager){
            Bukkit.getLogger().info("Initializing BlockBreakHandler");
            Bukkit.getPluginManager().registerEvents(this, plugin);
            _mineTogetherModeManager = mineTogetherModeManager;
            _fishTogetherModeManager = fishTogetherModeManager;
        }


    @EventHandler
    public void onChestBreak(BlockBreakEvent event)
    {

        Bukkit.getLogger().info("A block break event has occured.");

        Block block = event.getBlock();
        if(block.getType() != Material.CHEST)  return;

        Bukkit.getLogger().info("A Chest was broken");

        //We know we have a Chest
        Chest chest = (Chest) block.getState();

        if (chest.customName() != null ) {

            String chestName = chest.customName().toString();
            Bukkit.getLogger().info(chestName);

            //Mine Together Mode Chest Handler
            if (chestName.contains("Mine Together Mode Chest")) HandleMineTogetherModeChestBreak(chest);
            if (chestName.contains("Fishing Station")) HandleFishingStationChestBreak(chest);





        }
    }

    private void HandleFishingStationChestBreak(Chest chest) {
        Bukkit.getLogger().info("A Fishing Station Chest was broken");
        _fishTogetherModeManager.OnFishingStationRemoved(chest);
    }

    private void HandleMineTogetherModeChestBreak(Chest chest) {
        Bukkit.getLogger().info("A Mine Together Mode Chest was broken");
      //  _mineTogetherModeManager.
    }





}
