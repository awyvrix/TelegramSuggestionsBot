package net.awyvrix.bot.data.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SetSnapshot {
    private final Set<String> data = ConcurrentHashMap.newKeySet();

    public void addToSent(String id) {
        if (id != null) data.add(id);
    }

    public boolean contains(String id) {
        if (id == null) return false;
        return data.contains(id);
    }

    public Set<String> sent() {
        return data;
    }

    public Set<String> copy() {
        return Set.copyOf(data);
    }
}