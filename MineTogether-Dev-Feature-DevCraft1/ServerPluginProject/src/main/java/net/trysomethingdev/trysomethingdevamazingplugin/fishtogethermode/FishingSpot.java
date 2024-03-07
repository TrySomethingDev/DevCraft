package net.trysomethingdev.trysomethingdevamazingplugin.fishtogethermode;

import org.bukkit.Location;

//public class Fishing_Spots {
//
//    public String id;
//    public double location_x;
//    public double location_y;
//    public double location_z;
//    public String facing;
//    public Boolean is_occupied;
//    public String occupant_id;
//
//
//    public Fishing_Spots(String id, double locationX, double locationY, double locationZ, String facing, boolean isOccupied, String occupantId) {
//
//        this.id = id;
//        this.location_x = locationX;
//        this.location_y = locationY;
//        this.location_z = locationZ;
//
//        this.facing = facing;
//        this.is_occupied = isOccupied;
//        this.occupant_id = occupantId;
//    }
//}

public class FishingSpot {
    public String id;
    public double locationX;
    public double locationY;
    public double locationZ;
    public String facing;
    public boolean isOccupied;
    public String occupantId;
    public User user;

    public FishingSpot(String id, double locationX, double locationY, double locationZ, String facing, boolean isOccupied, String occupantId, User user) {

        this.id = id;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;

        this.facing = facing;
        this.isOccupied = isOccupied;
        this.occupantId = occupantId;
        this.user = user;
    }

}

