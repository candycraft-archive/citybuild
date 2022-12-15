/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 */
package net.twertion.trade.click.inventory;

import net.twertion.trade.user.User;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface IClickable {
    public void click(User var1, InventoryClickEvent var2);

    public boolean matches(User var1, Inventory var2);

    public Inventory getInventory(User var1);
}

