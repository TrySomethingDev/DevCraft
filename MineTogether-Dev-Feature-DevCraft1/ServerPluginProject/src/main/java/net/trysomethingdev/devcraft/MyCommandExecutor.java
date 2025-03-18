package net.trysomethingdev.devcraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;

            if (args.length == 1 && label.equalsIgnoreCase("gm")) {
                try {
                    GameMode gm = GameMode.valueOf(args[0]);
                    p.setGameMode(gm);
                    p.sendMessage(ChatColor.GREEN + "Your gamemode has been set to: " + gm.toString());
                    return true;
                } catch (IllegalArgumentException e) {
                    p.sendMessage(ChatColor.RED + "Invalid gamemode option!");
                    return false;
                }

            }
        }
        return false;
    }
}
