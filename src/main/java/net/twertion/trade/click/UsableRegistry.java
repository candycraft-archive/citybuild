/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package net.twertion.trade.click;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.twertion.trade.click.interact.IUsable;
import net.twertion.trade.user.User;
import org.bukkit.inventory.ItemStack;

public class UsableRegistry
extends ArrayList<IUsable> {
    private static final long serialVersionUID = 7670611767706604780L;

    public boolean registerObject(IUsable iUsable) {
        return this.add(iUsable);
    }

    public boolean unregisterObject(IUsable iUsable) {
        return this.remove(iUsable);
    }

    public void getObject(ItemStack itemStack, User user, Consumer<IUsable> consumer) {
        this.forEach(iClickable -> {
            if (iClickable.matches(user, itemStack)) {
                consumer.accept((IUsable)iClickable);
            }
        });
    }
}

