package net.trysomethingdev.trysomethingdevamazingplugin.handlers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

public class NpcHelper {
    public static void MoveClosestNPCOneBlockPostiveX(BlockPlaceEvent event, TrySomethingDevAmazingPlugin plugin) {
     var npc = GetNearestNPCToLocation(event.getBlock().getLocation());

     //Seems like you have to add 2 to the x in order to move one
     npc.getNavigator().setTarget(npc.getEntity().getLocation().add(2,0,0));


    }

    public static void MoveClosestNPCOneBlockNegativeX(BlockPlaceEvent event, TrySomethingDevAmazingPlugin plugin) {
        var npc = GetNearestNPCToLocation(event.getBlock().getLocation());
        //adding two to the x to go one.
        npc.getNavigator().setTarget(npc.getEntity().getLocation().add(-2,0,0));
    }


    private static NPC GetNearestNPCToLocation(Location location) {
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


}
