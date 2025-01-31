package net.trysomethingdev.devcraft.util;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
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

import java.util.concurrent.ThreadLocalRandom;

public class NpcMiningHelper {

    private NPC npc;

    public NpcMiningHelper(NPC npc) {
        this.npc = npc;
    }

    /**
     * Get the block at the NPC's current location.
     */
    public Block getBlockUnderneath() {
        Location location = npc.getEntity().getLocation();
        return location.getWorld().getBlockAt(location.clone().add(0, -1, 0));
    }

    /**
     * Get the block in front of the NPC at head height.
     */
    public Block getBlockInFrontAtHeadHeight() {
        return getBlockInFront(1);
    }

    /**
     * Get the block in front of the NPC at foot height.
     */
    public Block getBlockInFrontAtFootHeight() {
        return getBlockInFront(0);
    }

    /**
     * Get the block in front of the NPC at below foot height.
     */
    public Block getBlockInFrontAtBelowFootHeight()  {
        return getBlockInFront(-1);
    }
    /**
     * Get the block in front of the NPC below foot height.
     */
    public Block getBlockInFrontBelowFootHeight() {
        return getBlockInFront(-1);
    }

    /**
     * Get the block in front of the NPC above head height.
     */
    public Block getBlockInFrontAboveHeadHeight() {
        return getBlockInFront(2);
    }

    /**
     * Helper method to calculate the block in front of the NPC at a given height offset.
     *
     * @param heightOffset Offset from the NPC's foot location.
     * @return The block in front at the specified height offset.
     */
    private Block getBlockInFront(int heightOffset) {
        Location location = npc.getEntity().getLocation();
        World world = location.getWorld();

        if (world == null) return null;

        // Get the direction the NPC is facing and normalize it
        Location targetLocation = location.clone().add(location.getDirection().normalize());
        targetLocation.setY(location.getY() + heightOffset);

        return world.getBlockAt(targetLocation);
    }

    public NPC getNPC() {
        return this.npc;
    }

    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    // New methods to set NPC's facing direction
    public void faceNorth() {
        setFacingDirection(180);
    }

    public void faceSouth() {
        setFacingDirection(0);
    }

    public void faceEast() {
        setFacingDirection(-90);
    }

    public void faceWest() {
        setFacingDirection(90);
    }

    /**
     * Helper method to set the NPC's yaw (rotation angle).
     *
     * @param yaw The yaw angle to set.
     */
    private void setFacingDirection(float yaw) {
        if (npc.isSpawned()) {
            Location location = npc.getEntity().getLocation();
            location.setYaw(yaw);
            npc.getEntity().teleport(location);
        }
    }


}
