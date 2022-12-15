/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package net.twertion.trade.click.inventory;

import net.twertion.trade.click.inventory.IClickable;
import net.twertion.trade.user.User;
import org.bukkit.inventory.Inventory;

public abstract class AbstractClickableInventory
implements IClickable {
    protected final Inventory inventory;

    @Override
    public boolean matches(User user, Inventory inventory) {
        return inventory.getTitle().equals(this.inventory.getTitle());
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public AbstractClickableInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}

