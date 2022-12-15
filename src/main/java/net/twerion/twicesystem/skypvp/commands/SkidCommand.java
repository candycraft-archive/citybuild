/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.twerion.twicesystem.skypvp.commands;

import net.twerion.twicesystem.skypvp.skid.builder.LocationBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class SkidCommand implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("op")) {
            commandSender.sendMessage("\u00a7cBratan, du kannst diesen Command nicht benutzen.");
            return true;
        }
        if (args.length == 1) {
            List<String> arguments = Arrays.asList("top1", "top2", "top3", "top4", "top5", "rtop1", "rtop2", "rtop3", "rtop4", "rtop5", "save-all");
            if (arguments.contains(args[0])) {
                if (args[0].equalsIgnoreCase("save-all")) {
                    LocationBuilder.onDisablePlugin();
                    LocationBuilder.onEnablePlugin();
                    commandSender.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7aDie Locations wurden gespeichert & geladen!");
                    return true;
                }
                LocationBuilder.saveLocation((Player) commandSender, args[0].toLowerCase());
                return true;
            }
            commandSender.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7cRichtige Nutzung: /setup <rtop1, rtop2, rtop3, rtop4, rtop5, save-all>");
            return true;
        }
        commandSender.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7cRichtige Nutzung: /setup <rtop1, rtop2, rtop3, rtop4, rtop5, save-all>");
        return true;
    }
}

