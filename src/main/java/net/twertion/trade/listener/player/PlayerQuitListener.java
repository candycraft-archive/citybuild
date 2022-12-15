/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package net.twertion.trade.listener.player;

import net.twertion.trade.TradePlugin;
import net.twertion.trade.user.UserWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener
implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TradePlugin.getPlugin().getUserWrapper().remove((Object)event.getPlayer());
    }
}

