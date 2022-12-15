/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package net.twertion.trade.listener.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryCloseListener
implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().equals("Alchemist") && event.getInventory().getItem(13).getType() != Material.ENCHANTED_BOOK) {
            Arrays.asList(3, 5).forEach(slot -> {
                if (event.getInventory().getItem(slot.intValue()) != null) {
                    event.getPlayer().getInventory().addItem(new ItemStack[]{event.getInventory().getItem(slot.intValue())});
                }
            });
        }
    }
}

