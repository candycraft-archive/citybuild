/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  net.minecraft.server.v1_13_R2.ChatMessageType
 *  net.minecraft.server.v1_13_R2.EntityPlayer
 *  net.minecraft.server.v1_13_R2.IChatBaseComponent
 *  net.minecraft.server.v1_13_R2.IChatBaseComponent$ChatSerializer
 *  net.minecraft.server.v1_13_R2.Packet
 *  net.minecraft.server.v1_13_R2.PacketPlayOutChat
 *  net.minecraft.server.v1_13_R2.PlayerConnection
 *  org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package net.twerion.twicesystem.skypvp.utils;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarAPI {
    public static void sendActionBar(Player p, String msg) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a((String) ("{\"text\": \"" + msg + "\"}"));
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket((Packet) ppoc);
    }
}

