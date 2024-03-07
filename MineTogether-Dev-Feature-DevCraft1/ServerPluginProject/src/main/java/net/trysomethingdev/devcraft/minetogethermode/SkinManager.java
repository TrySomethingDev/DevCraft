package net.trysomethingdev.devcraft.minetogethermode;



// Import the necessary libraries
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinManager {


// Define a method that takes a URL and returns the id from the JSON response
private static String getIdFromUrl(String url) {
    try {
        // Create a URL object from the string
        URL obj = new URL(url);

        // Open a connection to the URL
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Set the request method to GET
        con.setRequestMethod("GET");

        // Read the response from the input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String lortu = response.toString();
        JSONParser parser = new JSONParser();
        JSONObject jo = (JSONObject) parser.parse(lortu);


        // Get the id value from the JSON object
        String id = jo.get("id").toString();

        // Return the id as a string

        return id;
    } catch (Exception e) {
        // Handle any exceptions
        e.printStackTrace();
        return null;
    }
}
    public static String GetUUIDForMinecraftUsername(String minecraftUserName) {

        var url = "https://api.mojang.com/users/profiles/minecraft/" + minecraftUserName;

        String id = SkinManager.getIdFromUrl(url);
        System.out.println("The id is: " + id);

        return id;
    }
}