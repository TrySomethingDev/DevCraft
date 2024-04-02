package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import net.trysomethingdev.devcraft.handlers.NpcHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@TraitName("pushtrait")
public class PushTrait extends Trait {

    public PushTrait() {
        super("pushtrait");
        }


    @Override
    public void run() {


    }

    public void pushNearestNPC()
    {

        if (!npc.isSpawned())  return;
        var block = npc.getEntity().getLocation().getBlock();

        //Find Nearest NPC
        var nearestNPC = GetNearestNPCToBlockThatIsNotItself(block);

        //Then apply a vector that would mimick a push

        var vector = new Vector(0.4,0,0);

        nearestNPC.getEntity().setVelocity(vector);


        npc.removeTrait(PushTrait.class);
    }

    private NPC GetNearestNPCToBlockThatIsNotItself(Block block) {

        NPC nearestNPC = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC currentNPC : CitizensAPI.getNPCRegistry()) {
            if (currentNPC.getName().equalsIgnoreCase(this.npc.getName())){
                continue;
            }
            double distance = currentNPC.getEntity().getLocation().distance(block.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNPC = currentNPC;
            }
        }
        return nearestNPC;
    }

    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }


    private static NPC GetNearestNPCToBlock(Block block) {
        NPC nearestNPC = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            double distance = npc.getEntity().getLocation().distance(block.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNPC = npc;
            }
        }
        return nearestNPC;
    }


    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {

    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
        //     Bukkit.dispatchCommand(npc.getEntity(),"say Hi I have unloaded.");
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {
        //    Bukkit.dispatchCommand(npc.getEntity(),"say Hi I have loaded.");
    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

}



