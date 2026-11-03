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

    public boolean switchAnon(long id, String username) {
        return getOrCreate(id, username).setAnonymous();
    }

    public boolean getAnon(long id, String username) {
        return getOrCreate(id, username).isAnonymous;
    }

    public void addAdmin(long id, String name) {
        getOrCreate(id, name).setAdmin(true);
    }

    public void addDeveloper(long id, String name) {
        getOrCreate(id, name).setDeveloper(true);
    }

    public boolean getAdmin(long id) {
        User user = get(id);
        if (user == null) return false;
        return user.isAdmin;
    }

    public boolean getDeveloper(long id) {
        User user = get(id);
        if (user == null) return false;
        return user.isDeveloper;
    }
}