/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.twertion.trade.api;

import java.io.PrintStream;
import net.twertion.trade.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum EMessage {
    PREFIX("\u00a78[\u00a7eTwerion\u00a78] \u00a7a"),
    CONSOLE_PREFIX("TradeUp >> ");
    
    private final String message;

    public static void toConsole(String message) {
        System.out.println(String.valueOf(CONSOLE_PREFIX.getMessage()) + message);
    }

    public static void toPlayer(Player player, String message) {
        player.sendMessage(String.valueOf(PREFIX.getMessage()) + message);
    }

    public static void toPlayer(Player player, EMessage message) {
        player.sendMessage(message.getMessage());
    }

    public static void toUser(User user, String message) {
        user.getPlayer().sendMessage(String.valueOf(PREFIX.getMessage()) + message);
    }

    public static void toAll(String message) {
        Bukkit.broadcastMessage((String)(String.valueOf(PREFIX.getMessage()) + message));
    }

    public String getMessage() {
        return this.message;
    }

    private EMessage(String message, int n2, String string2) {
        this.message = message;
    }
}

