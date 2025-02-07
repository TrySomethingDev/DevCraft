package net.trysomethingdev.devcraft.handlers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.traits.GuardTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class OnPlayerAttackedHandler implements Listener {

    private final DevCraftPlugin _plugin;


    public OnPlayerAttackedHandler(DevCraftPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerAttacked(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Entity attacker = event.getDamager();

        //Find NPC's defending the player

        for (NPC npc : getPlayerDefenders(player)) {
            if(npc == null || npc.getEntity() == null) continue;;
            if(npc.getEntity().getEntityId() == attacker.getEntityId()) continue;
            if(npc.getEntity().getType() == attacker.getType()) continue;

            npc.getOrAddTrait(GuardTrait.class).targetToAttack = attacker;


        }


    }

    private List<NPC> getPlayerDefenders(Player player) {

        List<NPC> defenders = new ArrayList<>();

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.hasTrait(GuardTrait.class)) {
                defenders.add(npc);
            }
        }

        return defenders;

    }
}