package net.trysomethingdev.devcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.trysomethingdev.devcraft.minetogethermode.items.ItemManager;

public class TutorialCommands implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only PLayers can use that command");
            return true;
        }

        Player player = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("givechest")){
            player.getInventory().addItem(ItemManager.chest);
        }

        if (command.getName().equalsIgnoreCase("givefishstation")){
            player.getInventory().addItem(net.trysomethingdev.devcraft.fishtogethermode.items.ItemManager.chest);
        }

        return true;
    }
}
