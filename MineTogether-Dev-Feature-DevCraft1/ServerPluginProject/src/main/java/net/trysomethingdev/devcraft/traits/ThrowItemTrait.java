package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ThrowItemTrait extends Trait {

    private final Material itemToThrow;
    private boolean hasRun;
    private int timeElapsed;

    public ThrowItemTrait() {
        super("ThrowItemTrait");
        this.itemToThrow = Material.COBBLESTONE; // Default item to throw
    }

    @Override
    public void run() {
        if (npc == null || !npc.isSpawned()) return;
        if(hasRun)return;
        Bukkit.broadcastMessage("Flinfsdfskdfsdindg");
        if(timeElapsed < 100) {
            timeElapsed++;
            return;
        }
        else{
           timeElapsed = 0;
        };
        Bukkit.broadcastMessage("Acting");


               new DelayedTask(() -> {
                   // Get NPC's current location
                   Location npcLocation = npc.getEntity().getLocation();
                   // Search for players within 3 blocks
                   for (Entity entity : npcLocation.getWorld().getNearbyEntities(npcLocation, 3, 3, 3)) {
                       if (entity instanceof Player player) {
                           // Throw the item at the player
                           hasRun = true;
                           throwItemAtPlayer(player);



                           break; // Only throw once per run cycle
                       }
                   }



        }, 20);





    }

    private void throwItemAtPlayer(Player player) {
        if (npc == null || !npc.isSpawned()) return;

        Bukkit.broadcastMessage("I am going to throw an item now!");
        // Get the NPC's current location
        Location npcLocation = npc.getEntity().getLocation();

        // Create the item to throw
        ItemStack itemStack = new ItemStack(itemToThrow, 1);
        Item thrownItem = npcLocation.getWorld().dropItem(npcLocation, itemStack);

        // Calculate the velocity to throw the item toward the player
        Vector direction = player.getLocation().toVector().subtract(npcLocation.toVector()).normalize();
        thrownItem.setVelocity(direction.multiply(2)); // Adjust the speed as needed

        npc.removeTrait(this.getClass());

        // Set a delay to remove the thrown item after a short time (e.g., 5 seconds)
//        new DelayedTask(() -> {
//            thrownItem.remove();
//        }, 200);

    }
}
