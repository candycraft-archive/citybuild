/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.twertion.trade.click.interact;

import net.twertion.trade.click.interact.IUsable;
import net.twertion.trade.user.User;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class AbstractUsableItem
implements IUsable {
    protected final ItemStack itemStack;

    @Override
    public boolean matches(User user, ItemStack itemStack) {
        return itemStack.getItemMeta().getDisplayName().equals(this.itemStack.getItemMeta().getDisplayName());
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public AbstractUsableItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}

