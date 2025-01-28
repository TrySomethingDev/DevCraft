package net.trysomethingdev.devcraft.services;

import net.citizensnpcs.api.ai.flocking.Flocker;
import net.citizensnpcs.api.ai.flocking.RadiusNPCFlock;
import net.citizensnpcs.api.ai.flocking.SeparationBehavior;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.traits.NPCBehaviorTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class NPCStateManager {
    private NPCBehaviorTrait behaviorTrait;
    private NPCState currentState;

    public NPCStateManager(NPC npc, NPCBehaviorTrait behaviorTrait) {
        this.currentState = NPCState.NOTHING; // Default state

        this.behaviorTrait = behaviorTrait;
    }

    public void transitionTo(NPCState newState,NPC npc) {
        if (currentState != newState) {
            onExitState(currentState);
            currentState = newState;
            onEnterState(newState,npc);
        }
    }

    public NPCState getCurrentState() {
        return currentState;
    }

    private void onEnterState(NPCState state,NPC npc) {
        // Handle logic for entering a state

        switch (state) {
            case FOLLOWING:
                // Start following logic

                break;
            case MINING:
                // Start mining logic
                break;
            case CHOPPING:
                // Start chopping logic
                break;
            case DANCING:
                // Start dancing animation
                break;
            case WAVING:
                // Start waving animation
                break;
            case FISHING:
                // Start fishing logic
                break;
            case NOTHING:
                // Stop all actions
                break;
        }
    }


    private void onExitState(NPCState state) {
        // Handle cleanup when exiting a state
        switch (state) {
            case FOLLOWING:
                // Stop following logic
                break;
            case MINING:
                // Stop mining logic
                break;
            case CHOPPING:
                // Stop chopping logic
                break;
            case DANCING:
                // Stop dancing animation
                break;
            case WAVING:
                // Stop waving animation
                break;
            case FISHING:
                // Stop fishing logic
                break;
            case NOTHING:
                // No cleanup necessary
                break;
        }
    }

//    @Persist("active")
//    private boolean enabled = true;
//    private Flocker flock;
//    @Persist
//    private UUID followingUUID;
//    private Player player;
//    @Persist
//    private boolean protect;
//
//    @Persist
//    private double margin = 5;
//
//    public boolean isActive(NPC npc) {
//        return enabled && npc.isSpawned() && player != null;
//    }
//    private void FollowMainPlayer(NPC npc) {
//
//        flock = new Flocker(npc, new RadiusNPCFlock(10), new SeparationBehavior(1000));
//
//
//        var player = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName());
//        if (player == null || !player.isValid()) {
//            if (followingUUID == null)
//                return;
//            player = Bukkit.getPlayer(followingUUID);
//            if (player == null) {
//                return;
//            }
//        }
//        if (!isActive(npc)) {
//            return;
//        }
//        if (!npc.getEntity().getWorld().equals(player.getWorld())) {
//            npc.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
//            return;
//        }
//        if (!npc.getNavigator().isNavigating() && npc.getEntity().getLocation().distance(player.getLocation()) >= margin) {
//            npc.getNavigator().setTarget(player.getLocation());
//
//        } else {
//            if (npc.getEntity().getLocation().distance(player.getLocation()) <= margin)
//            {
//                npc.getNavigator().cancelNavigation();
//            }
//            flock.run();
//        }
//    }


}