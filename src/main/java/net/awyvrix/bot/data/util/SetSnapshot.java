package net.awyvrix.bot.data.util;

import java.util.HashSet;
import java.util.Set;

public final class SetSnapshot {
    private final Set<String> data = new HashSet<>();

    public void addToSent(String id) {
        data.add(id);
    }

    public boolean contains(String id) {
        if (id == null) return false;
        return data.contains(id);
    }

    public Set<String> sent() {
        return data;
    }
}