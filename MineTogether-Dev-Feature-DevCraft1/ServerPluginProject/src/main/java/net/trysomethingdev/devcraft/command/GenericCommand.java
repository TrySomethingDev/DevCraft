package net.trysomethingdev.devcraft.command;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.citizensnpcs.api.trait.Trait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.util.DelayedTask;
import net.trysomethingdev.devcraft.util.NpcHelper;
import org.bukkit.Bukkit;

public class GenericCommand implements Command {
    private final Class<? extends Trait> traitClass;
    private final String commandName;

    public GenericCommand(String commandName, Class<? extends Trait> traitClass) {
        this.commandName = commandName;
        this.traitClass = traitClass;
    }

    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {
        new DelayedTask(() -> {
            var npcHelper = new NpcHelper();
            var npc = npcHelper.getNPCThatMatchesUser(user);
            if (npc == null) {
                Bukkit.broadcastMessage("Could not find NPC with Name " + user.twitchUserName);
                return;
            }

            npcHelper.removeTraitsResetHeadPositionAndRemoveToolFromInventory(npc);

            try {
                var trait = traitClass.getDeclaredConstructor().newInstance();
                npc.addTrait(trait);
                Bukkit.getLogger().info("Command executed: " + commandName + " | Trait applied: " + traitClass.getSimpleName());
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error applying trait: " + e.getMessage());
                e.printStackTrace();
            }
        }, 20);
    }
}
