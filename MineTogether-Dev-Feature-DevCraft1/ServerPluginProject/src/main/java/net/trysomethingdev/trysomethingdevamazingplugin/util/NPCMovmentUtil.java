package net.trysomethingdev.trysomethingdevamazingplugin.util;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.util.Vector;

public class NPCMovmentUtil
{
    public static void MoveX(NPC npc, double movementInXQty)
    {
        npc.getEntity().setVelocity(new Vector(1,0,0));
    }

}
