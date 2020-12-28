package com.townanim.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        NPC.showAllNPCs(event.getPlayer());
    }

}
