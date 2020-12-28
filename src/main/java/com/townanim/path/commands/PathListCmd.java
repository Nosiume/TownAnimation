package com.townanim.path.commands;

import com.townanim.path.Path;
import com.townanim.path.PathManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class PathListCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(args.length >= 1) {
                StringBuilder builder = new StringBuilder();
                for(String arg : args)
                    builder.append(arg + " ");
                String pathName = builder.toString().trim();

                Path path = PathManager.getPathByName(pathName);
                if(path == null) {
                    p.sendMessage("§cError : path \"" + pathName + "\" doesn't exist !");
                    return true;
                }

                p.sendMessage("§7=============================================");
                p.sendMessage("");
                List<Vector> points = path.getPoints();
                for(int i = 0 ; i < points.size() ; i++) {
                    Vector point = points.get(i);
                    TextComponent msg = new TextComponent("§7 - §e Point §9" + (i+1) + "§e : §7(X=§c" + point.getX() + "§7, Y=§c" + point.getY() + "§7, Z=§c" + point.getZ() + "§7)§e. §a[TELEPORT]");
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @s " + point.getX() + " " + point.getY() + " " + point.getZ()));
                    p.spigot().sendMessage(msg);
                }
                p.sendMessage("");
                p.sendMessage("§7=============================================");
                return true;
            }

            if(args.length == 0) {
                PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;

                if(PathManager.paths.size() == 0) {
                    p.sendMessage("§cThere are currently no paths listed on this server !");
                    return true;
                }

                p.sendMessage("§7=============================================");
                p.sendMessage("");
                for (Path path : PathManager.paths) {
                    String name = path.getName();
                    String json = "[{\"text\":\" - \",\"color\":\"gray\"}," +
                            "{\"text\":\"" + name + "\",\"color\":\"yellow\"}, {\"text\":\" (" + path.getPoints().size() + " points) \",\"color\":\"grey\"}," +
                            "{\"text\":\"[See All Points] \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/pathlist " + name + "\"}}," +
                            "{\"text\":\"[DELETE]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/deletepath " + name + "\"}}]";
                    PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json), ChatMessageType.CHAT, UUID.randomUUID());
                    conn.sendPacket(packet);
                }
                p.sendMessage("");
                p.sendMessage("§7=============================================\n");

                return true;
            }
        }
        sender.sendMessage("§cThis command is available for Players only !");
        return true;
    }
}
