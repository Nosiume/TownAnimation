package com.townanim.npc.commands;

import com.townanim.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateNpcCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            if(args.length <= 1)
                return false;

            Player p = (Player) sender;

            StringBuilder builder = new StringBuilder();
            for(int i = 1; i < args.length ; i++)
                builder.append(args[i] + " ");
            // get name and replace & to § to add color code control
            String name = builder.toString().trim().replace("&", "§");

            NPC npc = new NPC(name, p.getLocation(), NPC.getSkin(args[0]));
            p.sendMessage("§aSuccessfully Created NPC !");
            return true;
        }
        sender.sendMessage("§cSorry, you need to be a player to execute this command.");
        return true;
    }
}
