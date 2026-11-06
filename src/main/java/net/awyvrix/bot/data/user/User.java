package net.awyvrix.bot.data.user;

import java.util.HashSet;
import java.util.Set;

public final class User {
    public String username = "///unspecified";
    public Set<UserTag> userTags = new HashSet<>();
    public int sentContent = 0;
    public int approvedContent = 0;
    public float rating = 0;
    public int actions = 6;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public boolean switchTag(UserTag tag) {
        if (userTags.contains(tag)) userTags.remove(tag);
        else userTags.add(tag);
        return userTags.contains(tag);
    }

    public boolean getTag(UserTag tag) {
        return userTags.contains(tag);
    }

    public void setTag(UserTag tag, boolean isPresent) {
        if (isPresent) userTags.add(tag);
        else userTags.remove(tag);
    }

    public void updateUsername(String name) {
        if (username == null) username = name;
        if (!username.equals(name)) username = name;
    }

    public void addApproved() {
        approvedContent++;
    }

    public void addSent() {
        sentContent++;
    }
}