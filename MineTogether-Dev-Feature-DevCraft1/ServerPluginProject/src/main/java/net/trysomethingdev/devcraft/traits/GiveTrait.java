package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("givetrait")
public class GiveTrait extends Trait {

    private int jumpDelay;

    public GiveTrait() {
        super("givetrait");
    }


    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
    }

    @Override
    public void run() {
    }

    boolean alternator;
    int counter = 0;

    private void MainAction() {
        // Bukkit.broadcastMessage("Counter: " + counter);
        if (counter > 10) {
            npc.removeTrait(GiveTrait.class);
            return;
        }
        counter++;

        new DelayedTask(() -> {
        var mainPlayer = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName());

        //Give Cake to Dev
                mainPlayer.getInventory().addItem(new ItemStack(Material.CAKE));




        }, 3 * 1);
    }


    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        MainAction();
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



