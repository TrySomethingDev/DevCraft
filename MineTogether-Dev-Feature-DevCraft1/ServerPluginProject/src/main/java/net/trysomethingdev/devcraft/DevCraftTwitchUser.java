package net.trysomethingdev.devcraft;


import com.google.gson.annotations.Expose;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.trait.SkinTrait;
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
    private static Location npcGlobalSpawnPoint;
    @Expose public String twitchUserName;
    @Expose public String minecraftSkinName;
     public LocalDateTime lastActivityTime;
     public boolean isParted;
     public boolean isJoined;
     public boolean markedForDespawn;
    public boolean userWantsToPlay;
    public DevCraftTwitchUser(String twitchUserName, String minecraftSkinName, Location spawnLocation) {
        this.twitchUserName = twitchUserName;
        this.minecraftSkinName = minecraftSkinName;
        lastActivityTime = LocalDateTime.now();
        npcGlobalSpawnPoint = spawnLocation;
        this.JustJoinedOrIsActive();
    }

    public void Chatted() {
            this.JustJoinedOrIsActive();
    }

    private void createNPCWAndSpawnIt() {
        new DelayedTask(() -> {
            //IF NPC Does not exist in registry we need to make it.
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, this.twitchUserName);
            SpawnNPC(npc);
            AddSkinTrait(this.minecraftSkinName);
        }, 20);
    }

    private static void SpawnNPC(NPC npc) {
        var nextInt = ThreadLocalRandom.current().nextInt( 1, 2);
        new DelayedTask(() -> npc.spawn(npcGlobalSpawnPoint), 20L * nextInt);
    }

    private static void AddFollowerTrait(NPC npc) {
        new DelayedTask(() -> {
        FollowTraitCustom followTrait = new FollowTraitCustom(Bukkit.getPlayer("trysomethingdev"));
        npc.addTrait(followTrait);
        }, 20);
    }

    public NPC GetUserNPC() {
        var npcRegistry = CitizensAPI.getNPCRegistry();

        for (var npc : npcRegistry) {
            if (this.twitchUserName.equals(npc.getName())) {
                return npc;
            }
        }
        return null;
    }

    public void JustJoinedOrIsActive() {

        if(!userWantsToPlay)
        {
            return;
        }
        this.isJoined = true;
        this.isParted = false;
        this.lastActivityTime = LocalDateTime.now();
        if(markedForDespawn){
            Bukkit.broadcastMessage("Activity detected form Twitch user " + this.twitchUserName + "they are no longer marked for de-spawn");
            markedForDespawn = false;
        }
        NPC npc = getNPCThatMatchesName();

        if (npc != null && !npc.isSpawned()) {
            SpawnNPC(npc);
           // AddFollowerTrait(npc);
            AddSkinTrait(this.minecraftSkinName);
        }
        else  {
            this.createNPCWAndSpawnIt();
        }
    }

    private NPC getNPCThatMatchesName() {
        for (var npc : CitizensAPI.getNPCRegistry()) {
            if (this.twitchUserName.equals(npc.getName())) {
                return npc;
            }
        }
        return null;
    }

    public void Parted() {
        this.isJoined = false;
        this.isParted = true;
    }


    public void changeSkin(String skin) {
        this.minecraftSkinName = skin;
            AddSkinTrait(skin);
    }

    private void AddSkinTrait(String skinName) {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                var skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.clearTexture();
                skinTrait.setSkinName(skinName);
            }
        }, 20);
    }

    public void StartFishingCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                RemoveTraits(npc);
                npc.getOrAddTrait(FishTogetherTrait.class);
            }
        }, 20);
    }

    public void StartMineCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
               npc.getOrAddTrait(MinerTrait.class);
            }
        }, 20);

    }

    public void StartLoggingTreesCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                RemoveTraits(npc);
                npc.getOrAddTrait(LoggingTreesTrait.class);
            }
        }, 20);
    }

    public void StartEatingCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                npc.getOrAddTrait(EatingTrait.class);
            }
        }, 20);
    }

    public void StartBuildingCommand() {
    }

    public void QuarryCommand(int length, int width, int depth,DevCraftPlugin plugin) {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                if(npc.hasTrait(QuarryTrait.class)) npc.removeTrait(QuarryTrait.class);
                ResetHeadPosition(npc);
                RemoveTraits(npc);
                var quarry = new QuarryTrait(length,width,depth,plugin);
                npc.addTrait(quarry);
            }
        }, 20);
    }

    private static void RemoveTraits(NPC npc) {
        ResetHeadPosition(npc);
        RemoveToolFromInventorySpotZero(npc);
        if(npc.hasTrait(QuarryTrait.class)) npc.removeTrait(QuarryTrait.class);
        if(npc.hasTrait(FishTogetherTrait.class)) npc.removeTrait(FishTogetherTrait.class);
        if(npc.hasTrait(LoggingTreesTrait.class)) npc.removeTrait(LoggingTreesTrait.class);
        if(npc.hasTrait(DanceTrait.class)) npc.removeTrait(DanceTrait.class);
        if(npc.hasTrait(FollowTraitCustom.class)) npc.removeTrait(FollowTraitCustom.class);
        if(npc.hasTrait(UnloadTrait.class)) npc.removeTrait(UnloadTrait.class);
        if(npc.hasTrait(MinerTrait.class)) npc.removeTrait(MinerTrait.class);
        if(npc.hasTrait(StripMinerTrait.class)) npc.removeTrait(StripMinerTrait.class);
    }

    private static void ResetHeadPosition(NPC npc) {
        var rote = npc.getOrAddTrait(RotationTrait.class);
        float pitch = 0;
        float yaw = 0;
        npc.getOrAddTrait(RotationTrait.class).getPhysicalSession().rotateToHave(yaw, pitch);
    }

    private static void RemoveToolFromInventorySpotZero(NPC npc) {
        var eq = npc.getOrAddTrait(Equipment.class);
        eq.set(0, new ItemStack(Material.AIR));
    }

    public void DanceCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {


                ResetHeadPosition(npc);
                RemoveTraits(npc);

                var dance = new DanceTrait();
                npc.addTrait(dance);

            }
        }, 20);
    }

    public void FollowCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                if(npc.hasTrait(FollowTraitCustom.class))
                {
                    npc.removeTrait(FollowTraitCustom.class);
                    return;
                }
                ResetHeadPosition(npc);
                RemoveTraits(npc);
                AddFollowerTrait(npc);
            }
        }, 20);

    }

    public void UnloadIntoNearestChest() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                ResetHeadPosition(npc);
                RemoveTraits(npc);
                npc.getOrAddTrait(UnloadTrait.class);
            }
        }, 20);
    }

    public void Respawn() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                ResetHeadPosition(npc);
                RemoveTraits(npc);
                npc.despawn();

                //Respawn
                new DelayedTask(() -> {

                    if (npc != null) {
                    SpawnNPC(npc);
                    }
                }, 20 * 2);
            }
        }, 20);
    }
}