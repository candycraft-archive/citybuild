/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.twertion.trade.click.inventory.clickable;

import java.util.HashMap;
import net.twertion.trade.click.inventory.AbstractClickableInventory;
import net.twertion.trade.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public final class AlchemistClickable
extends AbstractClickableInventory {
    public AlchemistClickable(Inventory inventory) {
        super(inventory);
    }

    @Override
    public void click(User user, InventoryClickEvent event) {
        if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith("\u00a78*\u00a7fKlick\u00a78*")) {
            ItemStack itemStack = user.getTradeItem(event.getClickedInventory().getItem(3), event.getClickedInventory().getItem(5));
            event.getClickedInventory().setItem(13, itemStack);
            if (event.getClickedInventory().getItem(13).getType() == Material.ENCHANTED_BOOK) {
                ((Player)event.getWhoClicked()).closeInventory();
                ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ANVIL_USE, 1.0f, 2.0f);
                event.getWhoClicked().getInventory().addItem(new ItemStack[]{event.getClickedInventory().getItem(13)});
            }
        }
    }

    @Override
    public Inventory getInventory(User user) {
        return null;
    }
}

