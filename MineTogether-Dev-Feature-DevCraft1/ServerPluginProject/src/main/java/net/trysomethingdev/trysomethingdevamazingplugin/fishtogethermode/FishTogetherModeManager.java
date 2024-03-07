package net.trysomethingdev.trysomethingdevamazingplugin.fishtogethermode;


import com.denizenscript.denizen.nms.interfaces.FishingHelper;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
import net.trysomethingdev.trysomethingdevamazingplugin.denizen.FishTogetherTrait;
import net.trysomethingdev.trysomethingdevamazingplugin.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class FishTogetherModeManager {
    TrySomethingDevAmazingPlugin _plugin;
    String _yourMCPlayerName;
    private String _baseUrl;
    private List<InWorldFishingStation> _inWorldFishingStationList;
    private boolean _timerIsStarted = false;

    public FishTogetherModeManager(TrySomethingDevAmazingPlugin plugin, String yourMCPlayerName,String ApiBaseUrl) {
        _plugin = plugin;
        _yourMCPlayerName = yourMCPlayerName;
        _baseUrl = ApiBaseUrl;

        _inWorldFishingStationList = new ArrayList<>();


    }

    private void AddAndRemoveFisherMenFromStation() {

        var fishingSpots = GetListOfFishingSpots();

        for (FishingSpot fishingSpot : fishingSpots)
        {
            if(fishingSpot.isOccupied)
            {
               MakeSureFishingStationHasTheCorrectPersonInTheSpot(fishingSpot);
            }
            else
            {
                MakeSureFishingStationInWorldIsVacated(fishingSpot);
            }
        }
    }

    private void MakeSureFishingStationHasTheCorrectPersonInTheSpot(FishingSpot fishingSpot) {

        //An assumption is that the fishing spot is marked as occupied in the database
        Bukkit.getLogger().info("Trying to find in world fishing station with id of: " + fishingSpot.id);
       var inWorldFishingStation =  GetInWorldFishingStationById(fishingSpot.id);

//       var usersList = GetUsersList();


         if(inWorldFishingStation == null){
             Bukkit.getLogger().info("Could not find in world fishing station");
             return;
         }
        Bukkit.getLogger().info("Line 84");Bukkit.getLogger().info(inWorldFishingStation.toString());
        Bukkit.getLogger().info(String.valueOf(inWorldFishingStation.isOccupied));

         if(inWorldFishingStation.isOccupied)
         {
             //Do the player ID's match
             if(inWorldFishingStation.occupantId.equals(fishingSpot.occupantId))
             {
                 Bukkit.getLogger().info("Station is occupied and occupant_Ids match");
                 //Do nothing....everything matches.
             }
             else{
                 //We need to remove this NPC from the fishing station.
                 Bukkit.getLogger().info("Station is occupied but occupant_ids do not match");
                 RemoveNPCFromStation(inWorldFishingStation);
             }
         }
         else
         {
             //We need to add an NPC to this fishing station in world.
             Bukkit.getLogger().info("InWorldFishingStation Is marked as Not Occupied when FishingStatoin from DB is occupied. Need to add NPC");
             AddNPCToFishingStation(inWorldFishingStation,fishingSpot);
         }

    }



    private void AddNPCToFishingStation(InWorldFishingStation inWorldFishingStation, FishingSpot fishingSpot) {
        var world = Bukkit.getPlayer(_yourMCPlayerName).getWorld();
        var chestLocation = new Location(world,inWorldFishingStation.locationX
                ,inWorldFishingStation.locationY, inWorldFishingStation.locationZ);

        var npcName = fishingSpot.user.name;

        var npcLocation = chestLocation.add(0.5,0,1.5);

        var fishingLocation =  new Location(world, npcLocation.getX() + 10, npcLocation.getY(),npcLocation.getZ());

        var npcLocationWithYaw =  new Location(world, npcLocation.getX(), npcLocation.getY(),npcLocation.getZ(),270,0);
        //We assume at this point the station is facing east.

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcName);
        npc.spawn(npcLocationWithYaw);

        LocationTag location = new LocationTag(fishingLocation);

            ElementTag catchtype = new ElementTag("default");
            ElementTag stop = new ElementTag("false");
            ElementTag percent = new ElementTag("25");

            NPCTag npcTag = NPCTag.fromEntity(npc.getEntity());

            // AresNote: Created my own AresTrait to create the custom behavior
            FishTogetherTrait trait = npcTag.getCitizen().getOrAddTrait(FishTogetherTrait.class);
            if (stop.asBoolean()) {
                trait.stopFishing();
                return;
            }
            npcTag.getEquipmentTrait().set(0, new ItemStack(Material.FISHING_ROD));
            trait.setCatchPercent(percent.asInt());
            trait.setCatchType(FishingHelper.CatchType.valueOf(catchtype.asString().toUpperCase()));
            trait.startFishing(location);

        inWorldFishingStation.isOccupied = true;
        inWorldFishingStation.occupantId = fishingSpot.occupantId;
        inWorldFishingStation.user = fishingSpot.user;
    }

    private Users GetUserThatMatchesId(String occupantId, List<Users> usersList) {
        for (var user : usersList) {
            if(user.id.equals(occupantId)){
                return user;
            }
        }
      return null;
    }

    private void RemoveNPCFromStation(InWorldFishingStation inWorldFishingStation) {

        var npcRegistry = CitizensAPI.getNPCRegistry();
        for (NPC npc : npcRegistry) {
            if (npc.getName().equals(inWorldFishingStation.user.name)){
                npc.despawn();

                inWorldFishingStation.isOccupied = false;
                inWorldFishingStation.occupantId = "";
            }
        }


    }

    private InWorldFishingStation GetInWorldFishingStationById(String id) {

        Bukkit.getLogger().info("Looking at inWorldStationList with " + (long) _inWorldFishingStationList.size() + "stations on the list");

        for(var inWorldStation : _inWorldFishingStationList)
        {
            Bukkit.getLogger().info("Looking at inWorldStation Id: " + inWorldStation.id);
            if(inWorldStation.id.equals(id))
            {
                return inWorldStation;
            }
        }
        return null;
    }

    private void MakeSureFishingStationInWorldIsVacated(FishingSpot fishingSpot) {

        //An assumption is that the fishing spot is marked as occupied in the database
        var inWorldFishingStation =  GetInWorldFishingStationById(fishingSpot.id);

        if(inWorldFishingStation == null){
            Bukkit.getLogger().info("Could not find in world fishing station");
            return;
        }

        if(inWorldFishingStation.isOccupied)
        {
            RemoveNPCFromStation(inWorldFishingStation);
        }


    }


    public void OnNewFishingStationPlaced(Chest chest)  {

        Bukkit.getLogger().info("A Fishing Station has been placed");
        Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"),"say hello from fishTogetherManager!");

        var location = chest.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String facing = GetChestFacingDirection();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(_baseUrl + "/fishing/createSpawnPoint"))
                    .header("Content-Type", "application/json")
                    .method("POST",
                            HttpRequest.BodyPublishers.ofString("{\n  \"location\": {\n    \"x\": "+ x + ",\n    \"y\": "+ y + ",\n    \"z\": "+ z+ "\n  },\n \"facing\": \""+ facing +"\"   \n}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            Bukkit.getLogger().info(response.body());

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(response.body());

            if (jsonElement.isJsonObject()) {
                // Convert the JsonElement to a JsonObject
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // Get the "data" object from the JSON
                JsonObject dataObject = jsonObject.getAsJsonObject("data");

                // Get the value of the "id" field from the "data" object as a String
                String id = dataObject.get("id").getAsString();

                // Now you have the id stored in the 'id' variable
                System.out.println("ID: " + id);

                Bukkit.getLogger().info("Adding InWorldFishingStation to list");
                var inWorldFishingStation = new InWorldFishingStation(id, x, y, z, facing, false, "",null);
                _inWorldFishingStationList.add(inWorldFishingStation);
            }




        } catch (IOException e) {
            // Handle the exception here (e.g., log an error message or take other appropriate actions)
            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
        }

        if(_timerIsStarted == false)
        {
            _timerIsStarted = true;
            CreateNextDelayedTask();
        }


    }

    private void CreateNextDelayedTask(){
        new DelayedTask(() -> {
            Bukkit.getLogger().info("Running Timed Command");
            AddAndRemoveFisherMenFromStation();
            CreateNextDelayedTask();
        }, 20 * 5);
    }
    private String GetChestFacingDirection() {
        return "east";
    }

    public void OnFishingStationRemoved(Chest chest) {
        Bukkit.getLogger().info("A Fishing Station has been removed...calling API");
        //Bukkit.dispatchCommand(Bukkit.getPlayer("TrySomethingDev"),"say hello from fishTogetherManager!");
        var idToDelete = GetFishingStationIdByLocation(chest);
        DeleteFishingSpot(idToDelete);
    }

    private void DeleteFishingSpot(String idToDelete) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(_baseUrl + "/fishing/deleteSpawnPoint"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"id\": \""+ idToDelete + "\"\n}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            Bukkit.getLogger().info(response.body());

        } catch (IOException e) {
            // Handle the exception here (e.g., log an error message or take other appropriate actions)
            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
        }
    }

    private String GetFishingStationIdByLocation(Chest chest) {
        List<FishingSpot> fishingSpots = GetListOfFishingSpots();
        var locationWeAreLookingFor = chest.getLocation();
        return findId(fishingSpots
                ,locationWeAreLookingFor.getX()
                , locationWeAreLookingFor.getY()
                ,locationWeAreLookingFor.getZ());
    }

    public static String findId(List<FishingSpot> points, double x, double y, double z) {
        for (FishingSpot fishingSpots : points) {
            if (fishingSpots.locationX == x
                    && fishingSpots.locationY == y
                    && fishingSpots.locationZ == z) {
                return fishingSpots.id;
            }
        }
        return "-1"; // or any other value indicating not found
    }
//    private List<FishingStation> GetAllFishingStationLocations(Chest chest) {
//
//        try
//        {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(_baseUrl + "/fishing/getSpawnPoints"))
//                    .method("GET", HttpRequest.BodyPublishers.noBody())
//                    .build();
//            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println(response.body());
//            return response.body().toString();
//        }
//        catch (IOException e) {
//            // Handle the exception here (e.g., log an error message or take other appropriate actions)
//            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
//        }
//        catch (InterruptedException e) {
//            //throw new RuntimeException(e);
//            System.err.println("An error occurred while making the HTTP request: " + e.getMessage());
//        }
//
//        return "";
//    }
//private List<Users> GetUsersList() {
//
//    HttpRequest request = HttpRequest.newBuilder()
//            .uri(URI.create(_baseUrl + "/fishing/users"))
//            .method("GET", HttpRequest.BodyPublishers.noBody())
//            .build();
//    HttpResponse<String> response = null;
//    try {
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//       List<Users> usersList =  parseJsonResponseForUsers(response.body());
//       return usersList;
//
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//    }
//
//}

//    private List<Users> parseJsonResponseForUsers(String responseBody) {
//        List<Users> usersList = new ArrayList<>();
//
//        JsonParser parser = new JsonParser();
//        JsonElement jsonElement = parser.parse(responseBody);
//
//        if (jsonElement.isJsonArray()) {
//            JsonArray jsonArray = jsonElement.getAsJsonArray();
//            for (JsonElement element : jsonArray) {
//                JsonObject jsonObject = element.getAsJsonObject();
//                String id = jsonObject.get("id").getAsString();
//                String name = jsonObject.get("name").getAsString();
//                String minecraft_username = jsonObject.get("minecraft_username").getAsString();
//                String minecraft_uuid = jsonObject.get("minecraft_uuid").getAsString();
//
//                // Create a new Fishing_Spots object and add it to the list
//                Users user = new Users(id,name,minecraft_username,minecraft_uuid);
//
//                usersList.add(user);
//            }
//        }
//        return usersList;
//    }

    public List<FishingSpot> GetListOfFishingSpots() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(_baseUrl + "/fishing/getSpawnPoints"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        Type spawnPointListType = new TypeToken<List<FishingSpot>>(){}.getType();
        List<FishingSpot> fishingSpots = gson.fromJson(response.body(), spawnPointListType);

        return fishingSpots;


    }
}






