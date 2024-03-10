package net.trysomethingdev.devcraft;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.util.DelayedTask;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DevCraftTwitchUsersManager {

    private final DevCraftPlugin plugin;
    //List of Twitch Users here
    List<DevCraftTwitchUser> twitchUserList = new ArrayList<>();

    public DevCraftTwitchUsersManager(DevCraftPlugin devCraftPlugin) {

        plugin = devCraftPlugin;




    }

    public void DespawnTwitchUsersWhoHaveBeenInactiveTooLong() {

        new DelayedTask(() -> {

            for(var user : twitchUserList)
            {
                Duration duration = Duration.between(user.getLastActivityTime(), LocalDateTime.now());
                long minutes = duration.toMinutes();


                if(minutes > 20 && user.isMarkedForDespawn())
                {
                    NPC npc = user.GetUserNPC();
                    if(npc != null && npc.isSpawned())
                    {
                        npc.despawn();
                    }
                }
                else if (minutes > 19 && !user.isMarkedForDespawn())
                {
                    user.markUserForDespawn();
                }

            }

            //Call it again so it checks again after so much time.
            DespawnTwitchUsersWhoHaveBeenInactiveTooLong();


        }, 20 * 20);
    }


    //Add
public void Add(DevCraftTwitchUser twitchUser){
    if (!twitchUserList.contains(twitchUser))
    {
        twitchUserList.add(twitchUser);
    }
}

    //Remove
    public void Remove(DevCraftTwitchUser twitchUser){
        if (!twitchUserList.contains(twitchUser))
        {
            twitchUserList.remove(twitchUser);
        }
    }


    public void userJoined(String joinedNick) {
        var user = getUserByTwitchUserName(joinedNick);
        if(user == null)
        {  //If we make it here it means we did not find the user in the list. So we should add a user.
            twitchUserList.add(new DevCraftTwitchUser(joinedNick,joinedNick));
        }
        else
        {
            user.JustJoinedOrIsActive();
        }



    }

    private DevCraftTwitchUser getUserByTwitchUserName(String joinedNick) {
        for (var user : twitchUserList)
        {
            if(joinedNick.equals(user.getTwitchUserName())) {
                return user;
            }
        }
        return null;
    }

    public void userChatted(TwitchUser sender, TwitchMessage message) {

        //What kind of Command is this?

        //!SKIN MINECRAFTNAME
        Bukkit.getLogger().info("ReadingMessage");
        Bukkit.getLogger().info(message.getContent());

        if(message.getContent().startsWith("!"))
        {
            Bukkit.getLogger().info("This has message has been identified as a command");
            var command = message.getContent().toUpperCase();
            if(command.startsWith("!SKIN"))
            {
                var splitStringList = command.split(" ");
                //The Skin name we are going to use is the second word.
                var skin = splitStringList[1];
                var user = getUserByTwitchUserName(sender.getUserName());
                if(user != null){
                    user.changeSkin(skin);

                }
            }
        }
        else {
            Bukkit.getLogger().info("Not identified as a command");
        }

        var user = getUserByTwitchUserName(sender.getUserName());
        if(user == null) twitchUserList.add(new DevCraftTwitchUser(sender.getUserName(),sender.getUserName()));
        else user.Chatted();
    }

    public void userParted(String partedNick) {
        var user = getUserByTwitchUserName(partedNick);
        if(user != null) user.Parted();
    }
}
