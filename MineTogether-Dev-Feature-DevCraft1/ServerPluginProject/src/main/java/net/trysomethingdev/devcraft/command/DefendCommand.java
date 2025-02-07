package net.trysomethingdev.devcraft.command;

import com.denizenscript.denizen.scripts.commands.npc.VulnerableCommand;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.HologramTrait;
import net.trysomethingdev.devcraft.DevCraftPlugin;
import net.trysomethingdev.devcraft.models.DevCraftTwitchUser;
import net.trysomethingdev.devcraft.traits.GuardTrait;
import net.trysomethingdev.devcraft.util.NpcHelper;
import net.trysomethingdev.devcraft.util.NpcMiningHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DefendCommand implements Command {
    @Override
    public void execute(TwitchUser sender, TwitchMessage message, DevCraftTwitchUser user, DevCraftPlugin plugin, String arguments) {
        var npcHelper = new NpcHelper();
        npcHelper.getOrCreateNPCAndSpawnIt(user, Bukkit.getPlayer(plugin.getMainPlayerUserName()).getLocation());

       Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
           @Override
           public void run() {
               var npcHelper = new NpcHelper();
               var npc = npcHelper.getNPCThatMatchesUser(user);
               var eq = npc.getOrAddTrait(Equipment.class);
               eq.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
               eq.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
               eq.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
               eq.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
               eq.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.DIAMOND_LEGGINGS));

               var holo = npc.getOrAddTrait(HologramTrait.class);

                holo.addTemporaryLine("I will defend you!", 80);

               npc.getOrAddTrait(GuardTrait.class);




               npc.isProtected();
               npc.setProtected(false);
           }
       },60);


        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();
                var npc = npcHelper.getNPCThatMatchesUser(user);

                npc.removeTrait(GuardTrait.class);
                var eq = npc.getOrAddTrait(Equipment.class);
                eq.set(Equipment.EquipmentSlot.HAND, null);
                eq.set(Equipment.EquipmentSlot.CHESTPLATE,null);
                eq.set(Equipment.EquipmentSlot.BOOTS,null);
                eq.set(Equipment.EquipmentSlot.HELMET,null);
                eq.set(Equipment.EquipmentSlot.LEGGINGS,null);
                npc.removeTrait(Equipment.class);

                npc.removeTrait(HologramTrait.class);

            }
        },2200);


        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();
                var npc = npcHelper.getNPCThatMatchesUser(user);
                new NpcMiningHelper(npc).faceEast();
                var grav = npc.getOrAddTrait(Gravity.class);

                npc.getEntity().setVelocity(new Vector(0.5,0.5,0.5));
            }
        },2400);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();
                var npc = npcHelper.getNPCThatMatchesUser(user);

                npc.getEntity().setVelocity(new Vector(0,0.5,0));
            }
        },2400);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();
                var npc = npcHelper.getNPCThatMatchesUser(user);

                npc.getEntity().setVelocity(new Vector(0,10,0));
            }
        },2400);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();
                var npc = npcHelper.getNPCThatMatchesUser(user);
                npc.getEntity().setVelocity(new Vector(0,10,0));
                npc.getOrAddTrait(Gravity.class).setHasGravity(false);


            }
        },2400);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                var npcHelper = new NpcHelper();

                var npc = npcHelper.getNPCThatMatchesUser(user);

                npc.destroy();
            }
        },2500);




    }


}
