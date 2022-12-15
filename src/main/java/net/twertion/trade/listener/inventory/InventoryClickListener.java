/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.twertion.trade.listener.inventory;

import java.util.function.Consumer;
import net.twertion.trade.TradePlugin;
import net.twertion.trade.click.ClickableRegistry;
import net.twertion.trade.click.inventory.IClickable;
import net.twertion.trade.user.User;
import net.twertion.trade.user.UserWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener
implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final User user = (User)TradePlugin.getPlugin().getUserWrapper().get((Object)((Player)event.getWhoClicked()));
        if (event.getClickedInventory() != null && event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            TradePlugin.getPlugin().getClickableRegistry().getObject(event.getClickedInventory(), user, new Consumer<IClickable>(){

                @Override
                public void accept(IClickable iClickable) {
                    if (iClickable != null) {
                        iClickable.click(user, event);
                        event.setCancelled(true);
                    }
                }
            });
        }
    }

}

