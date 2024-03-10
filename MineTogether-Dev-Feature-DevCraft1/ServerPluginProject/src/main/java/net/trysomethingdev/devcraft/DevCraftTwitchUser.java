package net.trysomethingdev.devcraft;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.trait.SkinTrait;
import net.trysomethingdev.devcraft.traits.FollowTraitCustom;
import net.trysomethingdev.devcraft.traits.SkinTraitCustom;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class DevCraftTwitchUser {


    @Setter
    private String twitchUserName;

    @Setter
    private String minecraftSkinName;


    @Setter
    private LocalDateTime lastActivityTime;



    @Getter
    private boolean isParted;

    @Getter
    private boolean isJoined;

    @Getter
    private boolean markedForDespawn;

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
            AddFollowerTrait(npc);
        }, 20 * 2);
    }

    private static void SpawnNPC(NPC npc) {
        var nextInt = ThreadLocalRandom.current().nextInt( 1, 5);
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
        this.isJoined = true;
        this.isParted = false;
        this.lastActivityTime = LocalDateTime.now();

        if(isMarkedForDespawn()){
            Bukkit.broadcastMessage("Activity detected form Twitch user " + this.getTwitchUserName() + "they are no longer marked for de-spawn");
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
            AddFollowerTrait(npc);
        }
        else
        {
            //If npc is null
            this.createNPCWithFollowerTraitAndSpawnIt();
        }








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
        Bukkit.broadcastMessage("No Twitch Activity Detected for user " + this.getTwitchUserName() + "will de-spawn their player in 1 minute");
    }

    public void changeSkin(String skin) {

        this.setMinecraftSkinName(skin);


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

        new DelayedTask(() -> {

            var npc = GetUserNPC();
            if(npc != null) {

            }
        }, 20 * 2);

    }
}