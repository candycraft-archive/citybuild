/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package net.twertion.trade;

import java.util.Arrays;
import java.util.function.Consumer;
import net.twertion.trade.click.ClickableRegistry;
import net.twertion.trade.click.UsableRegistry;
import net.twertion.trade.click.inventory.clickable.AlchemistClickable;
import net.twertion.trade.command.AlchemistCommand;
import net.twertion.trade.listener.inventory.InventoryClickListener;
import net.twertion.trade.listener.inventory.InventoryCloseListener;
import net.twertion.trade.listener.player.PlayerJoinListener;
import net.twertion.trade.listener.player.PlayerQuitListener;
import net.twertion.trade.user.UserWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradePlugin
extends JavaPlugin {
    private static TradePlugin plugin;
    private UserWrapper userWrapper;
    private ClickableRegistry clickableRegistry;
    private UsableRegistry usableRegistry;

    public void onLoad() {
        plugin = this;
    }

    public void onEnable() {
        this.userWrapper = new UserWrapper();
        this.clickableRegistry = new ClickableRegistry();
        this.usableRegistry = new UsableRegistry();
        super.getCommand("alchemist").setExecutor((CommandExecutor)new AlchemistCommand());
        Arrays.asList(new InventoryCloseListener(), new InventoryClickListener(), new PlayerJoinListener(), new PlayerQuitListener()).forEach(spigotListener -> super.getServer().getPluginManager().registerEvents(spigotListener, (Plugin)this));
        this.clickableRegistry.add(new AlchemistClickable(Bukkit.createInventory(null, (InventoryType)InventoryType.CHEST, (String)"Alchemist")));
    }

    public static TradePlugin getPlugin() {
        return plugin;
    }

    public UserWrapper getUserWrapper() {
        return this.userWrapper;
    }

    public ClickableRegistry getClickableRegistry() {
        return this.clickableRegistry;
    }

    public UsableRegistry getUsableRegistry() {
        return this.usableRegistry;
    }

    public void setUserWrapper(UserWrapper userWrapper) {
        this.userWrapper = userWrapper;
    }

    public void setClickableRegistry(ClickableRegistry clickableRegistry) {
        this.clickableRegistry = clickableRegistry;
    }

    public void setUsableRegistry(UsableRegistry usableRegistry) {
        this.usableRegistry = usableRegistry;
    }
}

