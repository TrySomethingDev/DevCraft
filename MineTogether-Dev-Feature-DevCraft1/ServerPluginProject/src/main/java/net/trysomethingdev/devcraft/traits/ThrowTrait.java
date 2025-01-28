package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.util.PlayerAnimation;
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
@TraitName("throwtrait")
public class ThrowTrait extends Trait {

    private int jumpDelay;

    public ThrowTrait() {
        super("throwtrait");
    }


    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
    }

    @Override
    public void run() {
    }

    boolean alternator;
    int counter = 0;

    private void Throw() {
        // Bukkit.broadcastMessage("Counter: " + counter);
        if (counter > 10) {
            npc.removeTrait(ThrowTrait.class);
            return;
        }
        counter++;

        new DelayedTask(() -> {
        var mainPlayerLocation = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName()).getLocation();
            //Throw an Item
            throwItem(npc.getEntity().getLocation(),mainPlayerLocation , Material.STONE);
            Throw();

        }, 3 * 1);
    }

    public static void throwItem(Location location, Location target, Material material) {
        World world = location.getWorld();
        if (world == null) return;

        // Create an ItemStack of the item to be thrown
        ItemStack itemStack = new ItemStack(material);

        // Spawn the item entity in the world
        Item thrownItem = world.dropItem(location, itemStack);

        // Calculate the velocity vector towards the target
        Vector direction = target.toVector().subtract(location.toVector()).normalize();
        direction.multiply(1.5); // Adjust the speed multiplier as needed
        direction.setY(direction.getY() + 0.5); // Add an upward arc for realism

        // Set the velocity of the thrown item
        thrownItem.setVelocity(direction);

        // Optional: Set the item's pickup delay so players can't grab it instantly
        thrownItem.setPickupDelay(20); // 20 ticks = 1 second
    }
    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        Throw();
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



