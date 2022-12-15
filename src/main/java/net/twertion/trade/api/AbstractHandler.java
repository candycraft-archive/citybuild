/*
 * Decompiled with CFR 0_132.
 */
package net.twertion.trade.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class AbstractHandler
extends ConcurrentHashMap<Object, Object>
implements Serializable {
    private static final long serialVersionUID = 9214591514193682945L;

    public void forEach(Consumer<Object> consumer) {
        super.values().forEach(consumer);
    }

    public abstract void registerObject(Object var1, Object var2);

    public abstract void registerObject(Object var1);

    public abstract Object getObject(Object var1);

    public abstract Collection<Object> getObjects();
}

