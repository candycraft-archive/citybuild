/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.twertion.trade.click.interact;

import net.twertion.trade.user.User;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface IUsable {
    public void use(User var1, PlayerInteractEvent var2);

    public boolean matches(User var1, ItemStack var2);

    public ItemStack getItemStack(User var1);
}

