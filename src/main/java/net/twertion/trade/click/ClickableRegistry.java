/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package net.twertion.trade.click;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.twertion.trade.click.inventory.IClickable;
import net.twertion.trade.user.User;
import org.bukkit.inventory.Inventory;

public class ClickableRegistry
extends ArrayList<IClickable> {
    private static final long serialVersionUID = -4216751517765557585L;

    public boolean registerObject(IClickable iClickable) {
        return this.add(iClickable);
    }

    public boolean unregisterObject(IClickable iClickable) {
        return this.remove(iClickable);
    }

    public void getObject(Inventory inventory, User user, Consumer<IClickable> consumer) {
        this.forEach(iClickable -> {
            if (iClickable.matches(user, inventory)) {
                consumer.accept((IClickable)iClickable);
            }
        });
    }
}

