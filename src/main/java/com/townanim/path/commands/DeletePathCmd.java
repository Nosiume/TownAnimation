package com.townanim.path.commands;

import com.townanim.path.Path;
import com.townanim.path.PathManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeletePathCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 0)
            return false;

        StringBuilder builder = new StringBuilder();
        for(String arg : args)
            builder.append(arg + " ");
        String name = builder.toString().trim();

        Path path = PathManager.getPathByName(name);
        if(path == null) {
            sender.sendMessage("§cPath \"" + name + "\" doesn't exist.");
            return true;
        }
        PathManager.paths.remove(path);
        sender.sendMessage("§aSuccessfully deleted path §7" + name + "§a !");
        return true;
    }
}
