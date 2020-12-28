package com.townanim.path.commands;

import com.townanim.path.Path;
import com.townanim.path.PathManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PathCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            if(args.length < 1)
                return false;

            Player p = (Player) sender;

            if(PathManager.editing.containsKey(p)) {
                p.sendMessage("§cYou are already editing a path !");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for(String arg : args)
                builder.append(arg + " ");
            String name = builder.toString().trim();

            if(PathManager.getPathByName(name) != null) {
                p.sendMessage("§cThis path already exists !");
                return true;
            }

            Path path = new Path(name);
            PathManager.editing.put(p, path);

            p.sendMessage("§ePath §7" + name + " §ehas been created.");

            PathManager.enterEditingMode(p);
            return true;
        }
        sender.sendMessage("§cThis command only works for Player executors !");
        return true;
    }
}
