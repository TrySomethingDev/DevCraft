package net.trysomethingdev.devcraft.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.trysomethingdev.devcraft.TrySomethingDevAmazingPlugin;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
    @TraitName("foo")
    public class MyTrait extends Trait {
        public MyTrait() {
            super("foo");
            plugin = JavaPlugin.getPlugin(TrySomethingDevAmazingPlugin.class);
        }

        TrySomethingDevAmazingPlugin plugin = null;

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

           if(tickCounter % 60 == 0)
           {
               tickCounter = 1;



           //    float currentYaw =  npc.getEntity().getLocation().getYaw();
            //   float currentPitch = npc.getEntity().getLocation().getPitch();

//
//               currentYaw=  currentYaw + 5;
//               currentPitch =  currentPitch + 5;

             //  currentYaw=  currentYaw + 5;
    //           currentPitch =  currentPitch + 5;

               var playerLocation = Bukkit.getPlayer("TrySomethingDev").getLocation();
               npc.getEntity().setRotation(playerLocation.getYaw(), playerLocation.getPitch());
                     //  npc.getEntity().setRotation(currentYaw, currentPitch);
                       // Add code here to do whatever you want with this yaw and pitch

               Log("Pitch:" + npc.getEntity().getPitch() + " Yaw: " + npc.getEntity().getYaw());


               //Mine block in front of NPC
               var locationOfPlayersHead = npc.getEntity().getLocation().add(0,npc.getEntity().getHeight(),0);



    if(lastDirectionFacing != null)
    {
        Bukkit.getLogger().info("LastDirectionFacing = " + lastDirectionFacing);
    }

            //   Bukkit.getLogger().info("DirectionPlayerIsFacing = " + directionPlayerIsFacing);



              var result = npc.getEntity()
                      .getWorld()
                      .rayTraceBlocks(locationOfPlayersHead,npc.getEntity().getLocation().getDirection(),20);
              if(result != null)
              {
                 // npc.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK,result.getHitBlock().getLocation(),1);
                 // World w = npc.getEntity().getWorld(); /* get the World from a player or however you'd like */
                 // Location loc = npc.getEntity().getLocation().add(0,2,0); /* The location to spawn the particle */

                  var rayTraceSource = locationOfPlayersHead;
                  var rayTracedLocation = result.getHitBlock().getLocation();

              //    Bukkit.getLogger().info("RayTracedHitLocation: " + rayTracedLocation.toString());
                  Bukkit.getLogger().info(result.getHitBlock().getType().name());

                  drawLine(rayTraceSource,rayTracedLocation);





//                  w.spawnParticle(Particle.HEART, loc.add(1,3,0), 5, 0, 1, 0);
//                  w.spawnParticle(Particle.ASH, loc.add(2,3,0), 5, 0, 1, 0);
//                  w.spawnParticle(Particle.BLOCK_CRACK, loc.add(3,3,0), 100, 0, 1, 0, Material.DIAMOND_BLOCK.createBlockData());
//                  w.spawnParticle(Particle.BLOCK_MARKER, loc.add(4,3,0), 1, 0, 1, 0,Material.DIAMOND_BLOCK.createBlockData());
//                //  w.spawnParticle(Particle.BUBBLE_POP, loc.add(5,3,0), 100, 0, 1, 0,Material.TNT.createBlockData());
//
//
//                  w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc.add(6,3,0), 5, 0, 1, 0);
//
//                  w.spawnParticle(Particle.CLOUD, loc.add(7,3,0), 5, 0, 1, 0);
//
//                  w.spawnParticle(Particle.ELECTRIC_SPARK, loc.add(8,3,0), 5, 0, 1, 0);
//                  w.spawnParticle(Particle.FLAME, loc.add(15,3,0), 5, 0, 1, 0);


              }


           }

            //Jump after we have landed on the ground for a certain amount of time.


            //I think we just need to set the velocity in the y direction of the entity.

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



