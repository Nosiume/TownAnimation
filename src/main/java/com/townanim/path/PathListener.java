package com.townanim.path;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class PathListener implements Listener {

    // IMPORTANT : those events should only run if check() function returns TRUE for Player
    // Those events are made to be applied to a player only if he is in editing mode !

    //Check if player is in editing mode
    private boolean check(Player p) {
        return PathManager.editing.containsKey(p);
    }

    //Handles right click events with the items
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(check(p)) {
            ItemStack it = event.getItem();
            Action action = event.getAction();

            if(it == null)
                return;

            if(action == Action.RIGHT_CLICK_BLOCK) {
                if(it.isSimilar(PathManager.ADD_POINT_ITEM)) {
                    // Gets the location above the clicked block
                    Location l = event.getClickedBlock().getLocation().add(0, 1, 0);
                    Vector point = new Vector(l.getX(), l.getY(), l.getZ());
                    Path path = PathManager.editing.get(p);
                    if(path.isAlreadyInPath(point)) {
                        p.sendMessage("§cThis point is already in the path.");
                        event.setCancelled(true);
                        return;
                    }
                    path.addPoint(point);
                    p.sendMessage("§aPosition §7(" + point.getX() + " " + point.getY() + " " + point.getZ() + ") §a was successfully added to path points.");
                    PathManager.addMarker(p, point, PathManager.pointMarkers.get(p).size() + 1);
                } else if (it.isSimilar(PathManager.REMOVE_POINT_ITEM)) {
                    //Gets the location above the clicked block
                    Location l = event.getClickedBlock().getLocation().add(0, 1, 0);
                    Vector point = new Vector(l.getX(), l.getY(), l.getZ());
                    Path path = PathManager.editing.get(p);
                    if(!path.isAlreadyInPath(point)) {
                        p.sendMessage("§cThe point you're trying to remove is not in path's points.");
                        event.setCancelled(true);
                        return;
                    }
                    path.removePoint(point);

                    //We have to recreate all markers to put them back in the right order
                    PathManager.clearMarkers(p);
                    List<Vector> points = path.getPoints();
                    for(int i = 0 ; i < points.size() ; i++)
                        PathManager.addMarker(p, points.get(i), i+1);

                    p.sendMessage("§aPosition §7(" + point.getX() + " " + point.getY() + " " + point.getZ() + ") §a was successfully removed from path points.");
                }
            }
            else if( action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if(it.isSimilar(PathManager.SAVE_PATH_ITEM)) {
                    p.sendMessage("§ePath was successfully saved !");
                    PathManager.leaveEditingMode(p, true); // set save to true !
                } else if (it.isSimilar(PathManager.CANCEL_PATH_ITEM)) {
                    p.sendMessage("§ePath was successfully cancelled !");
                    PathManager.leaveEditingMode(p, false); // set save to false !
                }
            }
            event.setCancelled(true);
        }
    }

    //Avoid the player from dropping edit items
    @EventHandler
    public void onDropEvent(PlayerDropItemEvent event) {
        if(check(event.getPlayer()))
            event.setCancelled(true);
    }

}
