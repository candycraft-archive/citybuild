/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.twertion.trade.user;

import java.util.Collection;
import net.twertion.trade.api.AbstractHandler;
import net.twertion.trade.user.User;
import org.bukkit.entity.Player;

public class UserWrapper
extends AbstractHandler {
    private static final long serialVersionUID = 3550257120449634447L;

    @Override
    public void registerObject(Object key, Object value) {
        super.put(key, value);
    }

    @Override
    public void registerObject(Object value) {
        User user = (User)value;
        super.put(user.getPlayer(), user);
    }

    @Override
    public Object getObject(Object key) {
        return super.get(key);
    }

    @Override
    public Collection<Object> getObjects() {
        return super.values();
    }
}

