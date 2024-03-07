package net.trysomethingdev.trysomethingdevamazingplugin.handlers;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
import net.trysomethingdev.trysomethingdevamazingplugin.fishtogethermode.FishTogetherModeManager;
import net.trysomethingdev.trysomethingdevamazingplugin.minetogethermode.MineTogetherModeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestHandler implements Listener{

    private MineTogetherModeManager _mineTogetherModeManager;
    private FishTogetherModeManager _fishTogetherModeManager;
        public ChestHandler(TrySomethingDevAmazingPlugin plugin, MineTogetherModeManager mineTogetherModeManager,FishTogetherModeManager fishTogetherModeManager){
            Bukkit.getLogger().info("Initializing Chest Handler");
            Bukkit.getPluginManager().registerEvents(this, plugin);
            _mineTogetherModeManager = mineTogetherModeManager;
            _fishTogetherModeManager = fishTogetherModeManager;
        }

        @EventHandler
        public void onChestplace(BlockPlaceEvent event)
        {


            Block block = event.getBlock();
            if(block.getType() != Material.CHEST)  return;

            Bukkit.getLogger().info("A Chest was placed");
            //We know we have a Chest
            Chest chest = (Chest) block.getState();

            if (chest.customName() != null ) {

                String chestName = chest.customName().toString();
                Bukkit.getLogger().info(chestName);

                //Mine Together Mode Chest Handler
                if (chestName.contains("Mine Together Mode Chest")) HandleMineTogetherModeChestPlace(chest);
                if (chestName.contains("Fishing Station")) HandleFishingStationChestPlace(chest);





            }
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
        //_fishTogetherModeManager.
    }

    private void HandleMineTogetherModeChestBreak(Chest chest) {
        Bukkit.getLogger().info("A Mine Together Mode Chest was broken");
      //  _mineTogetherModeManager.
    }

    private void HandleFishingStationChestPlace(Chest chest) {
        Bukkit.getLogger().info("A Fishing Station Chest was placed");
        _fishTogetherModeManager.OnNewFishingStationPlaced(chest);
    }

    private void HandleMineTogetherModeChestPlace(Chest chest) {
        Bukkit.getLogger().info("A Mine Together Mode Chest was placed");
        _mineTogetherModeManager.start(chest);
    }


}
