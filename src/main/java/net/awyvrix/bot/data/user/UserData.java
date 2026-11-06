package net.awyvrix.bot.data.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserData {
    public static Map<Long, User> users = new ConcurrentHashMap<>();

    public static User getOrCreate(long id, String author) {
        if (!users.containsKey(id)) users.put(id, new User(author));
        return users.get(id);
    }

    public static User get(long id) {
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
}