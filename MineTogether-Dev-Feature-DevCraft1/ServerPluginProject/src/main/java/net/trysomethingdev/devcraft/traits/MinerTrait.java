package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("miner")
    public class MinerTrait extends Trait {
        public MinerTrait() {
            super("miner");
            plugin = JavaPlugin.getPlugin(DevCraftPlugin.class);
        }

        DevCraftPlugin plugin = null;

        boolean SomeSetting = false;

        // see the 'Persistence API' section
        @Persist("mysettingname") boolean automaticallyPersistedSetting = false;

        // Here you should load up any values you have previously saved (optional).
        // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
        // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
        // This is called BEFORE onSpawn, npc.getEntity() will return null.
        public void load(DataKey key) {
            SomeSetting = key.getBoolean("SomeSetting", false);
        }

        // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
        public void save(DataKey key) {
            key.setBoolean("SomeSetting",SomeSetting);
        }

        // An example event handler. All traits will be registered automatically as Spigot event Listeners
        @EventHandler
        public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
            //Handle a click on a NPC. The event has a getNPC() method.
            //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
            if(event.getNPC() == this.getNPC() )
            {
               Bukkit.getLogger().info("NPC CLICKED ON");
                NPCJump();
            }
        }



        // Called every tick
        private double YLastTick = 0;
        private int TicksAtSameY = 0;

        private int tickCounter = 1;
        private boolean IsOnGround = false;

   private Location lastDirectionFacing = null;
   private  Location locationPlayerIsStandingWithRandomVector = null;
        @Override
        public void run() {

         tickCounter++;
          //  Bukkit.getLogger().info("NPC Tick");

           if(tickCounter % 20 == 0)
           {
               tickCounter = 1;

               if(npc.getEntity().getLocation().getY() < -14)
               {
                   Log("We made it");
                   npc.removeTrait(this.getClass());
                   npc.addTrait(StripMinerTrait.class);
                   return;
               }
               Log("FIRING");



               centerPlayerOnBlock();
               //So the thought is we need to look
                //yaw 0
               //pitch 43
               npc.getEntity().setRotation(0,45);

              var height = npc.getEntity().getHeight();
              Log("Height: " + height);

              var locationWithHeight = npc.getEntity().getLocation().clone().add(0,height,0);
                //Then mine everything four blocks in front.
               //then move forward half a block...
               //mine four block ins front.

             ;

               // Make the NPC look down at 45 degrees
             // Vector direction = npc.getEntity().getLocation().getDirection();
              // direction.setY(-Math.tan(Math.toRadians(45)));
              // npc.faceLocation(npc.getEntity().getLocation().add(direction));

               // Mine 4 blocks that the NPC is looking at
               // for (int i = 0; i < 4; i++) {



               var result = npc.getEntity()
                       .getWorld()
                       .rayTraceBlocks(locationWithHeight,npc.getEntity().getLocation().getDirection(),4);
               if(result != null) {
                   // npc.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK,result.getHitBlock().getLocation(),1);
                   // World w = npc.getEntity().getWorld(); /* get the World from a player or however you'd like */
                   // Location loc = npc.getEntity().getLocation().add(0,2,0); /* The location to spawn the particle */

                   var rayTraceSource = locationWithHeight;
                   var rayTracedLocation = result.getHitBlock().getLocation();

                   Log(result.getHitBlock().toString());
                   if (result.getHitBlock().getType() != Material.AIR) {
                       result.getHitBlock().breakNaturally();
                   }


                   //    Bukkit.getLogger().info("RayTracedHitLocation: " + rayTracedLocation.toString());
                   Bukkit.getLogger().info(result.getHitBlock().getType().name());

                   drawLine(rayTraceSource, rayTracedLocation);
               } else {
                   // Move the NPC forward one block

                   npc.getEntity().setVelocity(getDirectionVector(npc.getEntity().getLocation()));
               }

                   // }


               //npc.getNavigator().setTarget(npc.getEntity().getLocation().add(npc.getEntity().getLocation().getDirection()));



           }



        }
    public double getCenteredCoordinate(double coord) {
        return Math.floor(coord) + 0.5;
    }
    public void centerPlayerOnBlock() {
        Location location = npc.getEntity().getLocation();
        double x = getCenteredCoordinate(location.getX());
        double z = getCenteredCoordinate(location.getZ());
        location.setX(x);
        location.setZ(z);
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    public Vector getDirectionVector(Location location) {
        double pitch = ((location.getPitch() + 90) * Math.PI) / 180;
        double yaw  = ((location.getYaw() + 90)  * Math.PI) / 180;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        return new Vector(x, z, y);
    }

    private static void Log(String s) {
        Bukkit.getLogger().info(s);
    }

    public void drawLine(Location point1, Location point2) {
        World world = point1.getWorld();
        double distance = point1.distance(point2);

        for(double d = 0; d <= 1; d += 0.5 / distance) {
            double x = point1.getX() + (point2.getX() - point1.getX()) * d;
            double y = point1.getY() + (point2.getY() - point1.getY()) * d;
            double z = point1.getZ() + (point2.getZ() - point1.getZ()) * d;

            world.spawnParticle(Particle.HEART, new Location(world, x, y, z), 1,0,0,0);
        }
    }
    private void NPCJump() {
        npc.getEntity().setVelocity(new Vector(0,1f,0));
    }

    //Run code when your trait is attached to a NPC.
        //This is called BEFORE onSpawn, so npc.getEntity() will return null
        //This would be a good place to load configurable defaults for new NPCs.
        @Override
        public void onAttach() {
       //     plugin.getServer().getLogger().info(npc.getName() + "has been assigned MyTrait!");
     //       Bukkit.dispatchCommand(npc.getEntity(),"say I have a new trait.");
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



