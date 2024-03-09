package net.trysomethingdev.twitchplugin.Encryption;


import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.twitchplugin.Data.DataManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;

public class EncryptionManager {

    public final String ENCRYPTION_KEY_PATH = "Encryption.key";
    public final String OAUTH_PATH = "Encryption.oauth";

    DevCraftPlugin plugin;
    DataManager dataManager;
    FileConfiguration config;

    public EncryptionManager() {
        this.plugin = DevCraftPlugin.getPlugin(DevCraftPlugin.class);
        this.dataManager = plugin.getDataManager();
        this.config = dataManager.getConfig();
    }

    public void generateNewKey() {
        ArrayList<Character> base = new ArrayList<>();
        for (char c : getBase().toCharArray()) {
            base.add(c);
        }

        StringBuilder keyBuilder = new StringBuilder();

        ArrayList<Character> newKey;

        newKey = new ArrayList<>(base);
        Collections.shuffle(newKey);

        String oAuth = reEncrypt(newKey);

        for (char c : newKey) {
            keyBuilder.append(c);
        }

        config.set(ENCRYPTION_KEY_PATH, keyBuilder.toString());
        config.set(OAUTH_PATH, oAuth);
        dataManager.saveConfig();
    }

    public String encrypt(String oauth, ArrayList<Character> newKey) {
        char[] oAuth = oauth.toCharArray();
        char[] base = getBase().toCharArray();
        StringBuilder oAuthBuilder = new StringBuilder();

        for (char c : oAuth) {
            for (int j = 0; j < base.length; j++) {
                if (c == base[j]) {
                    oAuthBuilder.append(newKey.get(j));
                    break;
                }
            }
        }
        return oAuthBuilder.toString();
    }

    public String reEncrypt(ArrayList<Character> newKey) {
        char[] oAuth = getOAuth().toCharArray();
        char[] base = getBase().toCharArray();
        StringBuilder oAuthBuilder = new StringBuilder();

        for (char c : oAuth) {
            for (int j = 0; j < base.length; j++) {
                if (c == base[j]) {
                    oAuthBuilder.append(newKey.get(j));
                    break;
                }
            }
        }
        return oAuthBuilder.toString();
    }

    private String getOAuthEncrypted() {
        return config.getString(OAUTH_PATH, "none");
    }

    public String getOAuth() {
        char[] base = getBase().toCharArray();
        char[] key = getKey().toCharArray();
        char[] oauth = getOAuthEncrypted().toCharArray();
        StringBuilder oauthBuilder = new StringBuilder();

        for (char c : oauth) {
            for (int j = 0; j < key.length; j++) {
                if (c == key[j]) {
                    oauthBuilder.append(base[j]);
                    break;
                }
            }
        }

        return oauthBuilder.toString().contains("oauth:") ? oauthBuilder.toString() : "oauth:" + oauthBuilder;
    }

    private String getBase() {
        StringBuilder baseBuilder = new StringBuilder();
        for (int c = 32; c < 127; c++) {
            baseBuilder.append((char) c);
        }
        return baseBuilder.toString();
    }

    public String getKey() {
        String key = config.getString(ENCRYPTION_KEY_PATH, null);

        return key != null ? key : getBase();
    }
}
