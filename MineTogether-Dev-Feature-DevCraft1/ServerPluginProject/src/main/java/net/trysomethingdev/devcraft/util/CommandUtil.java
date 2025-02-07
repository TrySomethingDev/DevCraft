package net.trysomethingdev.devcraft.util;

import org.bukkit.Bukkit;

import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.command.CommandConsumer;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;

@SuppressWarnings("deprecation")
public abstract class CommandUtil {
    public static void schedule(CommandConsumer<NpcHelper> fn, DevCraftTwitchUser user) {
        schedule(fn, user, 20);
    }

    public static void schedule(CommandConsumer<NpcHelper> fn, DevCraftTwitchUser user, int delay) {
        var helper = NpcHelper.getInstance();
        NPC npc = helper.getNPCThatMatchesUser(user);

        new DelayedTask(() -> {
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }
            fn.apply(helper, npc);
        }, delay);
    }
    

    public static void run(CommandConsumer<NpcHelper> fn, DevCraftTwitchUser user) {
        var helper = NpcHelper.getInstance();
        NPC npc = helper.getNPCThatMatchesUser(user);
        fn.apply(helper, npc);
    }
}
