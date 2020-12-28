package com.townanim.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private static List<NPC> NPCs = new ArrayList<NPC>();

    private EntityPlayer npc;
    private String IGN;

    public NPC(String IGN, Location loc, String[] skin) {
        this.IGN = IGN;

        this.createNPC(loc, skin);
    }

    public Player getAsPlayer() {
        return npc.getBukkitEntity().getPlayer();
    }
    public EntityPlayer getRawNPC() {
        return npc;
    }

    private void createNPC(Location loc, String[] skin) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), IGN);
        npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));

        //Apply Location
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        //Apply Skin
        profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));

        //Show the new NPC to the players
        for(Player p : Bukkit.getOnlinePlayers()) {
            showNPC(p, this);
        }
        NPCs.add(this);
    }

    //STATIC METHODS

    public static void showAllNPCs(Player p) {
        for(NPC npc : NPCs) {
            showNPC(p, npc);
        }
    }

    public static String[] getSkin(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};
        } catch (Exception e) {
            return new String[] {"error", "error"};
        }
    }

    private static void showNPC(Player p, NPC npc) {
        PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
        EntityPlayer rawNpc = npc.getRawNPC();
        conn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, rawNpc));
        conn.sendPacket(new PacketPlayOutNamedEntitySpawn(rawNpc));
        conn.sendPacket(new PacketPlayOutEntityHeadRotation(rawNpc, (byte) (rawNpc.yaw * 256 / 360)));
    }

}
