package net.trysomethingdev.devcraft.models;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class DevCraftTwitchUser {
    @Expose public String twitchUserName;
    @Expose public String minecraftSkinName;
    @Expose public int blocksMined;
    @Expose public int blocksMinedStone;
    @Expose public int blocksMinedIron;
    @Expose public int blocksMinedCopper;
    @Expose public int blocksMinedLapis;
    @Expose public int blocksMinedRedstone;
    @Expose public int blocksMinedDiamonds;
    @Expose public int logsChopped;
    @Expose public int fishCaught;


    public DevCraftTwitchUser(String twitchUserName, String minecraftSkinName) {
        this.twitchUserName = twitchUserName;
        this.minecraftSkinName = minecraftSkinName;
    }


    public void blockBrokenByUser(Material type) {
        blocksMined++;
        if(type.name().toUpperCase().contains("REDSTONE")) blocksMinedRedstone++;
        else if (type.name().toUpperCase().contains("STONE"))  blocksMinedStone++;
        else if(type.name().toUpperCase().contains("IRON")) blocksMinedIron++;
        else if(type.name().toUpperCase().contains("COPPER")) blocksMinedCopper++;
        else if(type.name().toUpperCase().contains("DIAMOND")) blocksMinedDiamonds++;
        else if(type.name().toUpperCase().contains("LAPIS")) blocksMinedLapis++;
        else if(type.name().toUpperCase().contains("LOG")) logsChopped++;
        else Bukkit.broadcastMessage("BlockType Not Tracked in Stats " + type.name());

    }


}