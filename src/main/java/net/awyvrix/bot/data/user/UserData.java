package net.awyvrix.bot.data.user;

import net.awyvrix.bot.data.BotState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserData {
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    public User getOrCreate(long id, String author) {
        synchronized (BotState.LOCK) {
            return users.computeIfAbsent(id, ignored -> new User(author));
        }
    }

    public User get(long id) {
        if (!users.containsKey(id)) return null;
        return users.get(id);
    }

    public boolean switchTag(long id, String username, UserTag tag) {
        return getOrCreate(id, username).switchTag(tag);
    }

    public boolean getTag(long id, String username, UserTag tag) {
        return getOrCreate(id, username).getTag(tag);
    }

    public void setTag(long id, String username, UserTag tag, boolean isPresent) {
        getOrCreate(id, username).setTag(tag, isPresent);
    }

    public Map<Long, User> users() {
        return users;
    }
}