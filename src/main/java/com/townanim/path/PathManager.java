package com.townanim.path;

import com.townanim.Constants;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PathManager {

    public static List<Path> paths = new ArrayList<Path>();
    public static Map<Player, Path> editing = new HashMap<Player, Path>();
    public static Map<Player, ItemStack[]> editingInvSave = new HashMap<Player, ItemStack[]>();
    public static Map<Player, List<ArmorStand>> pointMarkers = new HashMap<Player, List<ArmorStand>>();

    //Editing Items
    public static ItemStack ADD_POINT_ITEM = new ItemStack(Material.EMERALD, 1);
    public static ItemStack REMOVE_POINT_ITEM = new ItemStack(Material.REDSTONE, 1);
    public static ItemStack SAVE_PATH_ITEM = new ItemStack(Material.CHEST, 1);
    public static ItemStack CANCEL_PATH_ITEM = new ItemStack(Material.BARRIER, 1);

    public static void init() {
        ItemMeta addPointMeta = ADD_POINT_ITEM.getItemMeta();
        addPointMeta.setDisplayName("§aAdd Point");
        addPointMeta.setLore(Arrays.asList(new String[] {
                "§7Right-Click on a block to add ",
                "§7the position to the Path's points."
        }));
        ADD_POINT_ITEM.setItemMeta(addPointMeta);

        ItemMeta removePointMeta = REMOVE_POINT_ITEM.getItemMeta();
        removePointMeta.setDisplayName("§cRemove Point");
        removePointMeta.setLore(Arrays.asList(new String[] {
                "§7Right-Click on a block to remove ",
                "§7the position from the Path's points."
        }));
        REMOVE_POINT_ITEM.setItemMeta(removePointMeta);

        ItemMeta saveItemMeta = SAVE_PATH_ITEM.getItemMeta();
        saveItemMeta.setDisplayName("§eSave Path");
        saveItemMeta.setLore(Arrays.asList(new String[] {
                "§7Right-Click to save the freshly created path !"
        }));
        SAVE_PATH_ITEM.setItemMeta(saveItemMeta);

        ItemMeta cancelItemMeta = CANCEL_PATH_ITEM.getItemMeta();
        cancelItemMeta.setDisplayName("§eCancel Path");
        cancelItemMeta.setLore(Arrays.asList(new String[] {
                "§7Right-Click to cancel the current path."
        }));
        CANCEL_PATH_ITEM.setItemMeta(cancelItemMeta);
    }

    //========== EDITING MODE ========== //

    public static void enterEditingMode(Player p) {
        p.sendMessage("§9 - Entering path editing mode.");

        pointMarkers.put(p, new ArrayList<ArmorStand>());

        PlayerInventory inv = p.getInventory();
        editingInvSave.put(p, inv.getContents());
        inv.clear(); //if doesn't work try p.getInventory().clear()

        inv.setItem(0, SAVE_PATH_ITEM);
        inv.setItem(3, ADD_POINT_ITEM);
        inv.setItem(5, REMOVE_POINT_ITEM);
        inv.setItem(8, CANCEL_PATH_ITEM);
    }

    public static void leaveEditingMode(Player p, boolean save) {
        p.sendMessage("§9 - Leaving path editing mode.");

        if(save) {
            Path path = editing.get(p);
            paths.add(path);
        }
        editing.remove(p);

        ItemStack[] invSave = editingInvSave.get(p);
        p.getInventory().setContents(invSave);
        editingInvSave.remove(p);

        clearMarkers(p);
        pointMarkers.remove(p);
    }

    //Adds a marker to the player's view
    public static void addMarker(Player p, Vector point, int count) {
        World w = p.getWorld();
        ArmorStand stand = (ArmorStand) w.spawnEntity(new Location(w, point.getX() + 0.5, point.getY(), point.getZ() + 0.5), EntityType.ARMOR_STAND);

        stand.setCustomName("§aPoint §9" + count);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setInvisible(true);

        pointMarkers.get(p).add(stand);
    }

    //Clear all markers
    public static void clearMarkers(Player p) {
        //Remove the player's markers
        for(ArmorStand a : pointMarkers.get(p)) {
            a.remove();
        }
        pointMarkers.get(p).clear();
    }

    //============ DATA SAVING ============ //

    //Check if paths.json file exists and if it doesn't then creates it
    private static void checkSaveFile() {
        File file = new File(Constants.MCPATH_STORAGE_FILE);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Loads paths from paths.json file
    public static void loadPathData() {
        checkSaveFile();

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(Constants.MCPATH_STORAGE_FILE)){
            JSONObject obj = (JSONObject) parser.parse(reader);
            Iterator<String> keys = obj.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                JSONArray arr = (JSONArray) obj.get(key);
                paths.add(Path.fromJSONArray(key, arr));
            }
            System.out.println("Successfully loaded path data from paths.json file !");
        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    //Save paths to paths.json file
    public static void savePathData() {
        checkSaveFile();

        JSONObject obj = new JSONObject();
        for(Path p : paths) {
            obj.put(p.getName(), p.getAsJSONArray());
        }
        try(FileWriter file = new FileWriter(Constants.MCPATH_STORAGE_FILE);) {
            file.write(obj.toJSONString());
            file.flush();
            System.out.println("Successfully saved path data to paths.json file !");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // ========== UTILS =========== //

    public static Path getPathByName(String pathName) {
        for(Path p : paths) {
            if(p.getName().equals(pathName))
                return p;
        }
        return null;
    }

}
