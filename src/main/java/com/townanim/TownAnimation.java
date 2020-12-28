package com.townanim;

import com.townanim.npc.NPCListener;
import com.townanim.npc.commands.CreateNpcCmd;
import com.townanim.path.commands.DeletePathCmd;
import com.townanim.path.commands.PathCmd;
import com.townanim.path.PathListener;
import com.townanim.path.PathManager;
import com.townanim.path.commands.PathListCmd;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TownAnimation extends JavaPlugin {

    public static TownAnimation instance;

    @Override
    public void onEnable() {
        //Instance
        instance = this;

        //Setup Workspace
        File dir = new File(Constants.PLUGIN_FOLDER_PATH);
        if(!dir.exists())
            dir.mkdir();

        PathManager.init();
        PathManager.loadPathData();

        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        PathManager.savePathData();
    }

    private void registerCommands() {
        getCommand("createnpc").setExecutor(new CreateNpcCmd());

        //Paths
        getCommand("path").setExecutor(new PathCmd());
        getCommand("pathlist").setExecutor(new PathListCmd());
        getCommand("deletepath").setExecutor(new DeletePathCmd());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new NPCListener(), this);
        pm.registerEvents(new PathListener(), this);
    }
}
