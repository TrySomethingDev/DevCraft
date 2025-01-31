package net.trysomethingdev.devcraft.twitchconnection;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.google.gson.Gson;
import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

public class TwitchOAuth {
    private static final String REDIRECT_URI = "http://localhost:3000";
    private static final String AUTH_URL = "https://id.twitch.tv/oauth2/authorize";
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String SCOPES = "chat:edit chat:read";

    public static OAuthResponse getOAuthToken(String clientId, String clientSecret) {
        try {
            String state = "random_state_string"; // Generate a random state string for security
            String authLink = AUTH_URL + "?response_type=code" +
                    "&client_id=" + clientId +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.toString()) +
                    "&scope=" + URLEncoder.encode(SCOPES, StandardCharsets.UTF_8.toString()) +
                    "&state=" + state;

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(authLink));
            } else {
                System.out.println("Open this URL in your browser:");
                System.out.println(authLink);
            }

            return waitForAuthCode(clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static OAuthResponse waitForAuthCode(String clientId, String clientSecret) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        final String[] authCode = new String[1];

        server.createContext("/", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                authCode[0] = query.split("code=")[1].split("&")[0];
                String response = "Authorization successful. You can close this window.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                server.stop(0);
            }
        });

        server.start();
        while (authCode[0] == null) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }

        return requestAccessToken(clientId, clientSecret, authCode[0]);
    }

    private static OAuthResponse requestAccessToken(String clientId, String clientSecret, String authCode) throws IOException {
        String params = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + authCode +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.toString());

        String response = sendPostRequest(TOKEN_URL, params);
        Gson gson = new Gson();
        return gson.fromJson(response, OAuthResponse.class);
    }

    public static String refreshOAuthToken(String clientId, String clientSecret, String refreshToken) {
        try {
            String params = "client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&grant_type=refresh_token" +
                    "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8.toString());

            String response = sendPostRequest(TOKEN_URL, params);
            System.out.println("New Token Response: " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String sendPostRequest(String urlString, String urlParameters) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}