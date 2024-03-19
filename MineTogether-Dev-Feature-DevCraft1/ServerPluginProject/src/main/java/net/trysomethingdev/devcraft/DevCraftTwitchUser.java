package net.trysomethingdev.devcraft;


import com.google.gson.annotations.Expose;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.trysomethingdev.devcraft.denizen.FishTogetherTrait;
import net.trysomethingdev.devcraft.traits.*;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static net.trysomethingdev.devcraft.DevCraftPlugin.usersToIgnoreList;


public class DevCraftTwitchUser {


    @Expose public String twitchUserName;

    @Expose public String minecraftSkinName;

     public LocalDateTime lastActivityTime = LocalDateTime.now();
     public boolean isParted;

     public boolean isJoined;
     public boolean markedForDespawn;

    public boolean userWantsToPlay;



    public DevCraftTwitchUser()
    {

    }


    public DevCraftTwitchUser(String twitchUserName, String minecraftSkinName) {
        this.twitchUserName = twitchUserName;
        this.minecraftSkinName = minecraftSkinName;
        lastActivityTime = LocalDateTime.now();
        this.JustJoinedOrIsActive();

    }

    public void Chatted() {


            this.JustJoinedOrIsActive();



    }

    private void createNPCWithFollowerTraitAndSpawnIt() {
        new DelayedTask(() -> {
            //IF NPC Does not exist in registry we need to make it.
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, this.twitchUserName);
            SpawnNPC(npc);
         //   AddFollowerTrait(npc);
            AddSkinTrait(this.minecraftSkinName);
        }, 20 * 2);
    }

    private static void SpawnNPC(NPC npc) {
        var nextInt = ThreadLocalRandom.current().nextInt( 1, 2);
        new DelayedTask(() -> {
            npc.spawn(Bukkit.getPlayer("trysomethingdev").getLocation());

        }, 20 * nextInt);
    }

    private static void AddFollowerTrait(NPC npc) {
        new DelayedTask(() -> {
        FollowTraitCustom followTrait = new FollowTraitCustom(Bukkit.getPlayer("trysomethingdev"));
        npc.addTrait(followTrait);
        }, 20 * 3);
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

        if(IsOnIgnoreList() || !userWantsToPlay)
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


        //Make sure they have an NPC in the world.
        //Check to see if NPC is Spawned
        var npcRegistry = CitizensAPI.getNPCRegistry();

        NPC npc = getNPCThatMatchesName(this.twitchUserName);
        if(npc != null && npc.isSpawned()) return;
        else if (npc != null && !npc.isSpawned())
        {
            SpawnNPC(npc);
           // AddFollowerTrait(npc);
            AddSkinTrait(this.minecraftSkinName);
        }
        else
        {
            //If npc is null
            this.createNPCWithFollowerTraitAndSpawnIt();
        }








    }

    private boolean IsOnIgnoreList() {
        for (String userToIgnore :  usersToIgnoreList)
        {
            if(this.twitchUserName.toUpperCase().equals(userToIgnore.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private NPC getNPCThatMatchesName(String twitchUserName) {
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

    public void markUserForDespawn() {
        markedForDespawn = true;
        Bukkit.broadcastMessage("No Twitch Activity Detected for user " + this.twitchUserName + "will de-spawn their player in 1 minute");
    }

    public void changeSkin(String skin) {

        this.minecraftSkinName = skin;

            AddSkinTrait(skin);



    }

    private void AddSkinTrait(String skinName) {
        new DelayedTask(() -> {

            var npc = GetUserNPC();
            if(npc != null) {

                SkinTrait foo = npc.getOrAddTrait(SkinTrait.class);

                SkinTrait skinTrait = npc.getTrait(SkinTrait.class);

                skinTrait.clearTexture();
                skinTrait.setSkinName(skinName);

//                Bukkit.getLogger().info("Adding SKIN");
//                Bukkit.getLogger().info(npc.getName());
//
//                SkinTraitCustom skinTraitCustom = npc.getOrAddTrait(SkinTraitCustom.class);
//                new DelayedTask(() -> {
//
//                    var npc1 = GetUserNPC();
//                    if(npc1 != null) {
//                        skinTraitCustom.setSkinName(skinName,true);
//                        new DelayedTask(() -> {
//
//                            var npc2 = GetUserNPC();
//                            if(npc2 != null) {
//                                npc2.addTrait(skinTraitCustom);
//                            }
//                        }, 20 * 4);
//                    }
//                }, 20 * 3);
//
//
//



                Bukkit.getLogger().info(skinName);
                Bukkit.getLogger().info("Done adding SKIN");
            }
        }, 20 * 2);
    }

    public void StartFishingCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                RemoveTraits(npc);
                FishTogetherTrait testTrait = npc.getOrAddTrait(FishTogetherTrait.class);
            }
        }, 20 * 1);


    }

    public void StartMineCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                var trait = npc.getOrAddTrait(MinerTrait.class);
            }
        }, 20 * 1);

    }

    public void StartLoggingTreesCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if(npc != null) {
                RemoveTraits(npc);
                var trait = npc.getOrAddTrait(LoggingTreesTrait.class);
            }
        }, 20 * 1);
    }

    public void StartEatingCommand() {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {
                var trait = npc.getOrAddTrait(EatingTrait.class);
            }
        }, 20 * 1);

    }

    public void StartBuildingCommand() {
    }

    public void QuarryCommand(int length, int width, int depth) {
        new DelayedTask(() -> {
            var npc = GetUserNPC();
            if (npc != null) {

                if(npc.hasTrait(QuarryTrait.class))
                {
                    npc.removeTrait(QuarryTrait.class);
                }
                ResetHeadPosition(npc);
                RemoveTraits(npc);

                var quarry = new QuarryTrait(length,width,depth);
                npc.addTrait(quarry);

            }
        }, 20 * 1);


    }

    private static void RemoveTraits(NPC npc) {
        ResetHeadPosition(npc);
        RemoveToolFromInventorySpotZero(npc);
        if(npc.hasTrait(QuarryTrait.class)) npc.removeTrait(QuarryTrait.class);
        if(npc.hasTrait(FishTogetherTrait.class)) npc.removeTrait(FishTogetherTrait.class);
        if(npc.hasTrait(LoggingTreesTrait.class)) npc.removeTrait(LoggingTreesTrait.class);
        if(npc.hasTrait(DanceTrait.class)) npc.removeTrait(DanceTrait.class);
    }

    private static void ResetHeadPosition(NPC npc) {
        var rote = npc.getOrAddTrait(RotationTrait.class);
        var ph = rote.getPhysicalSession();
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
        }, 20 * 1);




    }
}