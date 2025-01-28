package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("findminetrait")
public class FindMineTrait extends Trait {

    private int jumpDelay;
    private BlockFace directionToMine;

    public FindMineTrait() {
        super("findminetrait");
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
            npc.removeTrait(FindMineTrait.class);
            return;
        }
        counter++;

        new DelayedTask(() -> {
        var mainPlayer = Bukkit.getPlayer(DevCraftPlugin.getInstance().getMainPlayerUserName());
        //Find Mining Sign
            //Find Item Frame
            //Find PickAxe Inside the Item Frame and get location.
           var location = findItemFrameWithItemInIt(new ItemStack(Material.WOODEN_PICKAXE),npc.getEntity().getLocation());

           var locationToStand = location.clone().add(0.5,-2,0.5);




           Location placeToStartMiningFrom = locationToStand.clone();

            float yaw = getYawFromBlockFace(directionToMine);
            placeToStartMiningFrom.setYaw(yaw);

            Bukkit.broadcastMessage(placeToStartMiningFrom.toString());

           //npc.getNavigator().setTarget(placeToStartMiningFrom);
            npc.teleport(placeToStartMiningFrom, PlayerTeleportEvent.TeleportCause.PLUGIN);

        }, 3 * 1);
    }
    private static float getYawFromBlockFace(BlockFace blockFace) {
        switch (blockFace) {
            case NORTH: return 180.0f;
            case EAST: return -90.0f;
            case SOUTH: return 0.0f;
            case WEST: return 90.0f;
            case NORTH_EAST: return -135.0f;
            case NORTH_WEST: return 135.0f;
            case SOUTH_EAST: return -45.0f;
            case SOUTH_WEST: return 45.0f;
            default: return 0.0f; // Default yaw
        }
    }

//    private Location findItemFrameWithItemInIt(ItemStack stack, Location center) {
//        for (Entity entity : center.getWorld().getNearbyEntities(center, 50, 50, 50)) {
//            if (entity instanceof ItemFrame itemFrame) {
//                ItemStack item = itemFrame.getItem();
//                if (item != null && item.isSimilar(stack)) {
//                    Block attachedBlock = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace());
//
//                        return attachedBlock.getLocation();
//
//                }
//            }
//        }
//        return null;
//    }


    private Location findItemFrameWithItemInIt(ItemStack stack, Location center) {
        for (Entity entity : center.getWorld().getNearbyEntities(center, 50, 50, 50, e -> e instanceof ItemFrame)) {
            ItemFrame itemFrame = (ItemFrame) entity; // Safely cast
            ItemStack item = itemFrame.getItem();

            if (item != null && item.isSimilar(stack)) {
                Block attachedBlock = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace());
                directionToMine = itemFrame.getAttachedFace();

                // Log or print the backside direction for debugging
                Bukkit.broadcastMessage("The miner should mine: " + directionToMine);

                return attachedBlock.getRelative(directionToMine.getOppositeFace()).getLocation(); // Return the location of the attached block
            }
        }
        return null; // Return null if no matching item frame is found
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



