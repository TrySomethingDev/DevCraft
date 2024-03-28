package net.trysomethingdev.devcraft.traits;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.interfaces.FishingHelper;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.util.PlayerAnimation;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.events.NpcFishEvent;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FishTogetherTrait extends Trait {
    private final DevCraftPlugin plugin;
    private boolean lookingForLocation = false;
    private boolean isSearchingForFishingLocation;
    private boolean scanning;

    public static NPC npcLookingForSpot;


    public FishTogetherTrait() {
        super("fishtogether");

            plugin = JavaPlugin.getPlugin(DevCraftPlugin.class);

    }

    private List<BlockFace> blockFacesToCheck = new ArrayList<>();



    @Persist("fishing")
    public boolean fishing = false;
    @Persist("catch type")
    public FishingHelper.CatchType catchType = FishingHelper.CatchType.DEFAULT;

    @Persist("fishing spot")
    public Location fishingLocation = null;

    public FishHook fishHook = null;
    public Item fish = null;

    @Persist("catch chance")
    public int catchPercent = 100;

    @Persist("reel tick rate")
    public int reelTickRate = 200;

    @Persist("cast tick rate")
    public int castTickRate = 75;

    int reelCount = 100;
    int castCount = 0;

    public boolean isCast = false;
  //  private Inventory inventory;

    @Override
    public void onAttach() {
        npc.removeTrait(FollowTraitCustom.class);
        npc.data().setPersistent(NPC.Metadata.PICKUP_ITEMS,true);

        //   inventory = Bukkit.createInventory(null, 36); // Create a new inventory for the NPC
        var eq = npc.getOrAddTrait(Equipment.class);
        eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.FISHING_ROD));

        NumberOfTimesSearchForNearbyFishingLocationHasBeenCalled = 0;
        InitializeBlockFacedToChecklist();

    }

    private void InitializeBlockFacedToChecklist() {
        blockFacesToCheck = new ArrayList<>();
        blockFacesToCheck.add(BlockFace.NORTH);
        blockFacesToCheck.add(BlockFace.SOUTH);
        blockFacesToCheck.add(BlockFace.EAST);
        blockFacesToCheck.add(BlockFace.WEST);
        blockFacesToCheck.add(BlockFace.NORTH_EAST);
        blockFacesToCheck.add(BlockFace.NORTH_WEST);
        blockFacesToCheck.add(BlockFace.SOUTH_EAST);
        blockFacesToCheck.add(BlockFace.SOUTH_WEST);
    }

    @Override
    public void onSpawn() {
        InitializeBlockFacedToChecklist();
        isCast = false;
        Log("****ONSPAWN*****");

        npc.removeTrait(FollowTraitCustom.class);
         npc.data().setPersistent(NPC.Metadata.PICKUP_ITEMS,true);

     //   inventory = Bukkit.createInventory(null, 36); // Create a new inventory for the NPC
        var eq = npc.getOrAddTrait(Equipment.class);
        eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.FISHING_ROD));

    }

    private int delaySetting = 20 * 10;
    private int delay;
    private int delay2;
    @Override
    public void run() {

      //  Log("Fire");
        //Pre-Checks
        if (!(npc.getEntity() instanceof Player)) return;
        if (!npc.isSpawned()) return;
        if (npc.getNavigator().isNavigating()) return;
        if (!CitizensAPI.getNPCRegistry().isNPC(npc.getEntity())) return;

        if(scanning) return;
     //   Log("Fired Fish " + "LookingForLocation: " + this.lookingForLocation + " Fishing: " + fishing);


        delay2++;

        if(delay2 > 20 * 30 && npcLookingForSpot == null && !isCast)
        {

            npc.removeTrait(FishTogetherTrait.class);
            npc.getOrAddTrait(FishTogetherTrait.class);
            npcLookingForSpot = npc;
            delay2 = 0;
        }

        //If we do not have a fishing location
        // AND we have not started looking for a fishing location yet...
        //We want to start searching for a fishing location.
        if(fishingLocation == null && !this.lookingForLocation && (npcLookingForSpot == null || npcLookingForSpot == npc))
        {
            npcLookingForSpot = npc;
          //  Log("Firing 1");
            //start searching for location
            fishing = false;

            GetFishingLocation();
        }
        else if(fishingLocation != null && !this.lookingForLocation) {
        //
          //  Log("Firing 2");
            //We are ready to fish.
            fishing = true;
        }
      //if(fishingLocation != null) Log(fishingLocation.toString());
    //  if(fishingLocation == null) Log("Fishing Location is null");
     // Log(String.valueOf(lookingForLocation));
        if(fishingLocation != null && this.lookingForLocation){
      //      Log("Are we close enough to valid ground?");
            if(npc.getEntity().getLocation().distance(validGroundToFishFrom) < 2){
         //       Log("Setting LookingForLocationToFalse");
                this.lookingForLocation = false;
            }
        }



        if (!fishing) {
          //  Log("We are not fishing");
          //  Log("Do we have a fishing locaiton?");
          //  if(fishingLocation != null) Log(fishingLocation.toString());
         //   Log("Not Fishing");
            isCast = false;
            return;
        }
        if (isCast) {
        //    Log("IsCast");
            if(npcLookingForSpot == npc)
            {
                npcLookingForSpot = null;
            }
            reelCount++;
            if (reelCount >= reelTickRate) {
                reel();
            //    Log("Checking Inventory");
                CheckIfInventoryIsFullAndIfFullSendThemBackToPlayer();
                reelCount = 0;
                castCount = 0;
            }
        }
        else {
         //   Log("Else");
            castCount++;
            if (castCount >= castTickRate) {
                cast();
                castCount = 0;
            }
        }
    }

    private void GetFishingLocation() {
            if(npc.getEntity().getLocation().distance(plugin.getFishingAreaStartPoint()) < 5 && !scanning){
                scanning = true;
                new DelayedTask(() -> {
               //     Log("Firing 3");
                    this.lookingForLocation = true;
                startFishing();
                }, 20 * 1);
            }
            else{
            //    Log("Firing 4");
                npc.getNavigator().setTarget(plugin.getFishingAreaStartPoint());
            }



    }

    private void CheckIfInventoryIsFullAndIfFullSendThemBackToPlayer() {
        var inventoryTrait = npc.getOrAddTrait(Inventory.class);
        var inventoryView = inventoryTrait.getInventoryView();
        if(inventoryView.firstEmpty() == -1 )
        {
          //  Log(npc.getName() + " Inventory is FULL!");
            npc.removeTrait(FishTogetherTrait.class);
            var eqip = npc.getOrAddTrait(Equipment.class);
            eqip.set(0, null);
          var trait =  npc.getOrAddTrait(FollowTraitCustom.class);

        }
    }

    private void Log(String message) {
        Bukkit.getLogger().info(message);
    }

    // <--[action]
    // @Actions
    // start fishing
    //
    // @Triggers when the NPC starts fishing. See also <@link command fish>.
    //
    // @Context
    // None
    //
    // -->

    /**
     * Makes the NPC fish at the specified location
     * <p/>
     * TODO Reimplement variance, so each cast doesn't land in the exact same spot.
     *
     * @param location the location to fish at
     */
    public void startFishing(Location location) {
        new NPCTag(npc).action("start fishing", null);
        fishingLocation = location.clone();
        cast();
        fishing = true;
    }

    // <--[action]
    // @Actions
    // stop fishing
    //
    // @Triggers when the NPC stops fishing. See also <@link command fish>.
    //
    // @Context
    // None
    //
    // -->

    /**
     * Makes the stop fishing.
     */
    public void stopFishing() {
        new NPCTag(npc).action("stop fishing", null);
        reelWithoutLoot();
        reelCount = 100;
        castCount = 0;
        fishingLocation = null;
        fishing = false;
        npc.removeTrait(FishTogetherTrait.class);
    }

    public boolean scanForFishSpot(Location near, boolean horizontal) {
        Block block = near.getBlock();
        if (block.getType() == Material.WATER) {
            fishingLocation = near.clone();
            return true;
        }
        else if (block.getRelative(BlockFace.DOWN).getType() == Material.WATER) {
            fishingLocation = near.clone().add(0, -1, 0);
            return true;
        }
        else if (block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.WATER) {
            fishingLocation = near.clone().add(0, -2, 0);
            return true;
        }
        if (horizontal) {
            return scanForFishSpot(near.clone().add(1, 0, 0), false)
                    || scanForFishSpot(near.clone().add(-1, 0, 0), false)
                    || scanForFishSpot(near.clone().add(0, 0, 1), false)
                    || scanForFishSpot(near.clone().add(0, 0, -1), false);
        }
        return false;
    }


    private boolean findNearestWaterWithinRadius(Location startingLocation,int radius) {
        Material blockTypeWeAreLookingFor = Material.WATER;
        var closestLocation = SearchForMaterialInRaidus(startingLocation, radius,-1,3, blockTypeWeAreLookingFor);


        if (closestLocation == null) return false;

     //   Log("FindNearestWaterWithinRadius");
     //   Log("closest location: " + closestLocation.getLocation());
        this.fishingLocation = closestLocation.getLocation().clone();
        return true;

    }

    private static Block SearchForMaterialInRaidus(Location location, int radius,int ylower, int yhigher, Material blockTypeWeAreLookingFor) {
        // ylower is how much lower than the current Y we should search.
        // yhigher is how much above the current Y we should search

        Block closestBlockOfSpecifiedMaterial = null;
        double closestDistance = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = ylower; y <= yhigher; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                    if (block.getType() == blockTypeWeAreLookingFor) {

                        double distance = block.getLocation().distance(location);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestBlockOfSpecifiedMaterial = block;
                        }



                    }
                }
            }
        }
        return closestBlockOfSpecifiedMaterial;
    }

    public void startFishing() {
        Location search = npc.getEntity().getLocation().clone();
     //   Log("Scanning for fishing spot");

        SearchForNearbyFishingLocation(search);

    }

    private int NumberOfTimesSearchForNearbyFishingLocationHasBeenCalled = 0;
    private void SearchForNearbyFishingLocation(Location search) {

        NumberOfTimesSearchForNearbyFishingLocationHasBeenCalled++;
// Log("Search For Nearby Fishing Location Function Called " + NumberOfTimesSearchForNearbyFishingLocationHasBeenCalled + "times");

        var locationFarthestFromAllEntities = search;
        var bestFishingLocation = search;
        double currentClosestEntity = 0;
        var foundLocation = false;
        Location startingPlayerLocation = npc.getEntity().getLocation().clone();
        //Currently this is searching just in the direction they are facing.
        for (int x = -20; x < 20; x++) {
            for (int i = -20; i < 20; i++) {

            Location spotToCheck = new Location(npc.getEntity().getWorld(),x,-61,i).add(startingPlayerLocation.getX(),0,startingPlayerLocation.getZ())   ;
   //         Log("SPOT TO CHECK " + spotToCheck.getX() + "," + spotToCheck.getY() + "," +spotToCheck.getZ() );

            if (spotToCheck.getBlock().getType() == Material.WATER)
            {
            //    Log("We found water");
                fishingLocation = spotToCheck.clone();
                foundLocation = true;

            }
            else
            {

                foundLocation = false;
            }
      //      search.add(new Location(npc.getEntity().getWorld(),x,0,i));
            //Log("direction: " + search.getDirection());

          //  var foundLocation = findNearestWaterWithinRadius(search,10* i);

            if(foundLocation)
            {
               // Log("We found a fishing spot");
               // Log("Fishing Spot is: " + fishingLocation.toString());
                //We found water....but now can we find dry land to fish from?
                //Air above///Not Water Below
                if(fishingLocation.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR)
                {
                //    Log("There is air above the fishing spot");
                    boolean foundSpot = CheckAllDirectionAroundThisSpotforGroundAndTwoAirBlocksVertically();
                    if(foundSpot)
                    {
                   //     Log("We have place to stand");
                        //Is there anyone else near that spot
                        var nearbyEntities = npc.getEntity().getWorld().getNearbyEntities(validGroundToFishFrom,20,20,20);                        double closestEntityDistance = 10;
                        for(var ent : nearbyEntities){
                            if(CitizensAPI.getNPCRegistry().isNPC(ent)){
                                {
                                    var distance = ent.getLocation().distance(validGroundToFishFrom);
                                    if(distance < closestEntityDistance)
                                    {
                                        closestEntityDistance = distance;
                                    }
                                }
                            }

                        }

                        if(closestEntityDistance > currentClosestEntity)
                        {
                            currentClosestEntity = closestEntityDistance;
                            locationFarthestFromAllEntities = validGroundToFishFrom.clone();
                            bestFishingLocation = fishingLocation;
                        }
                       // if(nearbyEntities.isEmpty()){

                        //    Log("WE FOUND SPOT WITH NO ENTITIES");
                         //   Log(validGroundToFishFrom.toString());

                       // }
                     //   Log("DID NOT FIND  SPOT WITH NO ENTITIES");
                      //  foundSpot = false;


                    }
                }
            }
        }
        }

        Log("FINISHED LOOKING AT LOCATIONS");
        Log("The best spot is " + currentClosestEntity +"Blocks away from any entity");
        Log(String.valueOf(locationFarthestFromAllEntities));
        Log(String.valueOf(fishingLocation));
        fishingLocation = bestFishingLocation;
        WalkToLocationTask(locationFarthestFromAllEntities);
        this.isSearchingForFishingLocation = false;
        return;

    }



    private Location validGroundToFishFrom;
    private boolean CheckAllDirectionAroundThisSpotforGroundAndTwoAirBlocksVertically() {

        for(var blockFace : blockFacesToCheck)
        {
            Boolean foundSpot = CheckBlockForValidFishingStandingLocation(fishingLocation.getBlock().getRelative(blockFace));
            if(foundSpot)
            {
            //    Log("We found a good spot to stand from");
                return true;
            }
        }

        return false;
    }

    private Boolean CheckBlockForValidFishingStandingLocation(Block block) {
        if(block.getType() != Material.WATER &&
                block.getType() != Material.AIR)
        {
            var result =  CheckForTwoBlockOfAirAboveBlock(block);
            if(result)
            {
                validGroundToFishFrom = block.getLocation();
                return true;
            }
        }

        return false;
    }

    private boolean CheckForTwoBlockOfAirAboveBlock(Block block) {
        if(block.getRelative(BlockFace.UP).getType() == Material.AIR  &&
                block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() == Material.AIR){
            return true;
        }

        return false;
    }

    private void WalkToLocationTask(Location destination)
    {

        npc.removeTrait(FollowTraitCustom.class);
        npc.removeTrait(FollowTrait.class);

        new DelayedTask(() -> {

            var result = npc.getNavigator().canNavigateTo(destination);
            Log("Can Navigate To = " + result);
            Log(destination.toString());
            npc.getNavigator().setTarget(destination);
            scanning = false;
            lookingForLocation = false;
        }, 20 * 1);

    }

    // <--[action]
    // @Actions
    // cast fishing rod
    //
    // @Triggers when the NPC casts a fishing rod. See also <@link command fish>.
    //
    // @Context
    // None
    //
    // -->
    private void cast() {
      //  Log("Casting out");
        new NPCTag(npc).action("cast fishing rod", null);
        if (fishingLocation == null || fishingLocation.getWorld() == null || !fishingLocation.getWorld().equals(npc.getEntity().getWorld())) {
            Debug.echoError("Fishing location not found!");
            return;
        }
        isCast = true;
        double v = 34;
        double g = 20;
        Location from = npc.getEntity().getLocation();
        from = from.add(0, 0.33, 0);
        Location to = fishingLocation;
        Vector test = to.clone().subtract(from).toVector();
        double elev = test.getY();
        Double testAngle = launchAngle(from, to, v, elev, g);
        if (testAngle == null) {
            return;
        }
        double hangtime = hangtime(testAngle, v, elev, g);
        Vector victor = to.clone().subtract(from).toVector();
        double dist = Math.sqrt(Math.pow(victor.getX(), 2) + Math.pow(victor.getZ(), 2));
        elev = victor.getY();
        if (dist == 0) {
            return;
        }
        Double launchAngle = launchAngle(from, to, v, elev, g);
        if (launchAngle == null) {
            return;
        }
        victor.setY(Math.tan(launchAngle) * dist);
        victor = normalizeVector(victor);
        v += 0.5 * Math.pow(hangtime, 2);
        v += (CoreUtilities.getRandom().nextDouble() - 0.8) / 2;
        victor = victor.multiply(v / 20.0);

        if (npc.getEntity() instanceof Player) {
            fishHook = NMSHandler.fishingHelper.spawnHook(from, (Player) npc.getEntity());
            fishHook.setShooter((ProjectileSource) npc.getEntity());
            fishHook.setVelocity(victor);
            PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
        }
    }

    // <--[action]
    // @Actions
    // reel in fishing rod
    //
    // @Triggers when the NPC reels in its fishing rod. See also <@link command fish>.
    //
    // @Context
    // None
    //
    // -->

    private void reelWithoutLoot() {
        isCast = false;
        Log("Reeling in wihtout loot");
        new NPCTag(npc).action("reel in fishing rod", null);
        if (fishHook != null && fishHook.isValid()) {
            fishHook.remove();
            fishHook = null;
        }
        if (npc.getEntity() instanceof Player) {
            PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
        }
    }


    // <--[action]
    // @Actions
    // catch fish
    //
    // @Triggers when the NPC catches a fish. See also <@link command fish>.
    //
    // @Context
    // None
    //
    // -->
    private void reel() {
        isCast = false;
      //  Log("Reeling in");
        new NPCTag(npc).action("reel in fishing rod", null);
        int chance = (int) (Math.random() * 100);
        //Log("CatchType Set to: "+ catchType.toString());

        if (catchPercent > chance && fishHook != null && catchType != FishingHelper.CatchType.NONE) {
            try {
                fish.remove();
            }
            catch (Exception e) {
            }
            Location location = fishHook.getLocation();
//
//            ItemStack diamond = new ItemStack(Material.DIAMOND_BLOCK);
//            ItemMeta diamondMeta = diamond.getItemMeta();
//            if (diamondMeta != null) {
//                Component displayName = Component.text("A RARE TREASURE");
//                diamondMeta.setDisplayName(displayName.toString());
//                diamond.setItemMeta(diamondMeta);
//            }

            ItemStack result = NMSHandler.fishingHelper.getResult(fishHook, catchType);
            if (result != null) {
                //AresNote: Important: to use the diamond replace "result" with "diamond"
                fish = location.getWorld().dropItem(location, result);
                Location npcLocation = npc.getStoredLocation();
                double d5 = npcLocation.getX() - location.getX();
                double d6 = npcLocation.getY() - location.getY();
                double d7 = npcLocation.getZ() - location.getZ();
                double d8 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
                double d9 = 0.1D;
                // AresNote: Increased velocity from 0.03D to 0.1D
                fish.setVelocity(new Vector(d5 * d9, d6 * d9 + Math.sqrt(d8) * 0.1D, d7 * d9));

                //AresNote: Create and fire custom event
                NpcFishEvent npcFishEvent = new NpcFishEvent((Player) npc.getEntity(), fish);
                Bukkit.getServer().getPluginManager().callEvent(npcFishEvent);
            }
            new NPCTag(npc).action("catch fish", null);
        }
        if (fishHook != null && fishHook.isValid()) {
            fishHook.remove();
            fishHook = null;
        }
        if (npc.getEntity() instanceof Player) {
            PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
        }
    }

    public boolean isFishing() {
        return fishing;
    }



    public static Double launchAngle(Location from, Location to, double v, double elev, double g) {
        Vector victor = from.clone().subtract(to).toVector();
        double dist = Math.sqrt(Math.pow(victor.getX(), 2) + Math.pow(victor.getZ(), 2));
        double v2 = Math.pow(v, 2);
        double v4 = Math.pow(v, 4);
        double derp = g * (g * Math.pow(dist, 2) + 2 * elev * v2);

        if (v4 < derp) {
            return null;
        }
        else {
            return Math.atan((v2 - Math.sqrt(v4 - derp)) / (g * dist));
        }
    }

    public static double hangtime(double launchAngle, double v, double elev, double g) {
        double a = v * Math.sin(launchAngle);
        double b = -2 * g * elev;

        if (Math.pow(a, 2) + b < 0) {
            return 0;
        }

        return (a + Math.sqrt(Math.pow(a, 2) + b)) / g;
    }

    public static Vector normalizeVector(Vector victor) {
        double mag = Math.sqrt(Math.pow(victor.getX(), 2) + Math.pow(victor.getY(), 2) + Math.pow(victor.getZ(), 2));
        if (mag != 0) {
            return victor.multiply(1 / mag);
        }
        return victor.multiply(0);
    }

    public void setCatchType(FishingHelper.CatchType catchType) {
        this.catchType = catchType;
    }

    public void setCatchPercent(int catchPercent) {
        this.catchPercent = catchPercent;
    }
}
