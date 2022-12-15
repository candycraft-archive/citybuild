/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.twertion.trade.command;

import net.twertion.trade.TradePlugin;
import net.twertion.trade.api.EMessage;
import net.twertion.trade.user.User;
import net.twertion.trade.user.UserWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlchemistCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        try {
            User user = (User)TradePlugin.getPlugin().getUserWrapper().getObject((Object)((Player)commandSender));
            user.openTradeUp();
        }
        catch (Exception ignored) {
            commandSender.sendMessage(String.valueOf(EMessage.PREFIX.getMessage()) + "\u00a7cUm diesen Befehl nutzen zu k\u00f6nnen, musst Du einmal rejoinen. :c");
        }
        return true;
    }
}

