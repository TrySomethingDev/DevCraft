package net.trysomethingdev.devcraft.util;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NpcHelper {
    public void moveClosestNPCOneBlockPostiveX(BlockPlaceEvent event, DevCraftPlugin plugin) {
        var npc = getNearestNPCToLocation(event.getBlock().getLocation());

        //Seems like you have to add 2 to the x in order to move one
        npc.getNavigator().setTarget(npc.getEntity().getLocation().add(2, 0, 0));


    }

    public NPC getNPCThatMatchesUser(DevCraftTwitchUser user) {
        var npcRegistry = CitizensAPI.getNPCRegistry();

        for (var npc : npcRegistry) {
            if (user.twitchUserName.equalsIgnoreCase(npc.getName())) {
                return npc;
            }
        }
        return null;
    }

    public void moveClosestNPCOneBlockNegativeX(BlockPlaceEvent event, DevCraftPlugin plugin) {
        var npc = getNearestNPCToLocation(event.getBlock().getLocation());
        //adding two to the x to go one.
        npc.getNavigator().setTarget(npc.getEntity().getLocation().add(-2, 0, 0));
    }


    private NPC getNearestNPCToLocation(Location location) {
        NPC nearestNPC = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            double distance = npc.getEntity().getLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNPC = npc;
            }
        }
        return nearestNPC;
    }


    public void removeTraitsResetHeadPositionAndRemoveToolFromInventory(NPC npc) {
        //Bukkit.broadcastMessage("Removing All Traits");
        resetHeadPosition(npc);
        removeToolFromInventorySpotZero(npc);

//        List<Trait> traits = new ArrayList<>();
//        npc.getTraits().forEach(traits::add);
//
//        for (Trait trait : traits)
//        {
//            npc.removeTrait(trait.getClass());
//        }

        if (npc.hasTrait(QuarryTrait.class)) npc.removeTrait(QuarryTrait.class);
        if (npc.hasTrait(FishTogetherTrait.class)) npc.removeTrait(FishTogetherTrait.class);
        if (npc.hasTrait(LoggingTreesTrait.class)) npc.removeTrait(LoggingTreesTrait.class);
        if (npc.hasTrait(DanceTrait.class)) npc.removeTrait(DanceTrait.class);
        if (npc.hasTrait(Dance2Trait.class)) npc.removeTrait(Dance2Trait.class);
        if (npc.hasTrait(Dance3Trait.class)) npc.removeTrait(Dance3Trait.class);
        if (npc.hasTrait(FollowTrait.class)) npc.removeTrait(FollowTrait.class);
        if (npc.hasTrait(FollowCustomTrait.class)) npc.removeTrait(FollowCustomTrait.class);
        if (npc.hasTrait(UnloadTrait.class)) npc.removeTrait(UnloadTrait.class);
        if (npc.hasTrait(MinerTrait.class)) npc.removeTrait(MinerTrait.class);
        if (npc.hasTrait(StripMinerTrait.class)) npc.removeTrait(StripMinerTrait.class);

        if (npc.hasTrait(WaveTrait.class)) npc.removeTrait(WaveTrait.class);
        if (npc.hasTrait(SpinTrait.class)) npc.removeTrait(SpinTrait.class);
        if (npc.hasTrait(TakeItemFromChestTrait.class)) npc.removeTrait(TakeItemFromChestTrait.class);
        if (npc.hasTrait(FindChestTrait.class)) npc.removeTrait(FindChestTrait.class);
        if (npc.hasTrait(SortingTrait.class)) npc.removeTrait(SortingTrait.class);
        if (npc.hasTrait(ThrowItemTrait.class)) npc.removeTrait(ThrowItemTrait.class);
        if (npc.hasTrait(NPCBehaviorTrait.class)) npc.removeTrait(NPCBehaviorTrait.class);
        if (npc.hasTrait(ThrowTrait.class)) npc.removeTrait(ThrowTrait.class);
        if (npc.hasTrait(GuardTrait.class)) npc.removeTrait(GuardTrait.class);
        if (npc.hasTrait(MinerV2Trait.class)) npc.removeTrait(MinerV2Trait.class);


    }

    public void resetHeadPosition(NPC npc) {
        var rote = npc.getOrAddTrait(RotationTrait.class);
        float pitch = 0;
        float yaw = 0;
        npc.getOrAddTrait(RotationTrait.class).getPhysicalSession().rotateToHave(yaw, pitch);
    }

    public void removeToolFromInventorySpotZero(NPC npc) {
        var eq = npc.getOrAddTrait(Equipment.class);
        eq.set(0, new ItemStack(Material.AIR));
    }

    public void spawnNPC(NPC npc, Location spawnLocation) {
        var nextInt = ThreadLocalRandom.current().nextInt(1, 2);
        new DelayedTask(() -> npc.spawn(spawnLocation), 20L * nextInt);
    }

    public void addFollowerTrait(NPC npc) {
        new DelayedTask(() -> {
            FollowCustomTrait followTraitCustom = new FollowCustomTrait(Bukkit.getPlayer("trysomethingdev"));
            npc.addTrait(followTraitCustom);
        }, 20);
    }


    public void addSkinTrait(DevCraftTwitchUser user, String skinName) {
        new DelayedTask(() -> {
            var npc = getNPCThatMatchesUser(user);
            if (npc != null) {
                var skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.clearTexture();
                Bukkit.broadcastMessage("Skin Name " + skinName);
                skinTrait.setSkinName(skinName);
            }
        }, 20);
    }

    public void getOrCreateNPCAndSpawnIt(DevCraftTwitchUser user, Location spawnLocation) {
        new DelayedTask(() -> {
            //IF NPC Does not exist in registry we need to make it.

            NPC npc = getNPCThatMatchesUser(user);
            if (npc == null) {
                npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, user.twitchUserName);
            }

            spawnNPC(npc, spawnLocation);
            addSkinTrait(user, user.minecraftSkinName);
        }, 20);
    }


    public void changeSkin(DevCraftTwitchUser user, String skin) {
        user.minecraftSkinName = skin;
        addSkinTrait(user, skin);
    }



}
