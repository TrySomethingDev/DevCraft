package net.trysomethingdev.devcraft.models;


import com.google.gson.annotations.Expose;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.traits.FishTogetherTrait;
import net.trysomethingdev.devcraft.traits.*;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class DevCraftTwitchUser {
    @Expose public String twitchUserName;
    @Expose public String minecraftSkinName;
    @Expose public int blocksMined;

    @Expose public int fishCaught;


    public DevCraftTwitchUser(String twitchUserName, String minecraftSkinName) {
        this.twitchUserName = twitchUserName;
        this.minecraftSkinName = minecraftSkinName;
    }



















}