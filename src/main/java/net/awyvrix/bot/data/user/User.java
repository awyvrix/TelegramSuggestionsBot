package net.awyvrix.bot.data.user;

import net.awyvrix.bot.data.config.ConfigManager;

import java.util.HashSet;
import java.util.Set;

public final class User {
    public String username = "[unspecified]";
    public Set<UserTag> userTags = new HashSet<>();
    public int sentContent = 0;
    public int approvedContent = 0;
    public float rating = 0;
    public int actions = ConfigManager.config.BASIC_ACTIONS;

    public User() {}

    public User(String username) {
        if (username == null) username = "[unspecified]";
        this.username = username;
    }

    public synchronized User copy() {
        User copy = new User(username);
        copy.userTags.addAll(userTags);
        copy.sentContent = sentContent;
        copy.approvedContent = approvedContent;
        copy.rating = rating;
        copy.actions = actions;
        return copy;
    }

    public synchronized boolean switchTag(UserTag tag) {
        if (userTags.contains(tag)) userTags.remove(tag);
        else userTags.add(tag);
        return userTags.contains(tag);
    }

    public synchronized boolean getTag(UserTag tag) {
        return userTags.contains(tag);
    }

    public synchronized void setTag(UserTag tag, boolean isPresent) {
        if (isPresent) userTags.add(tag);
        else userTags.remove(tag);
    }

    public synchronized void updateUsername(String name) {
        if (name == null) name = "[unspecified]";
        if (username == null) username = name;
        if (!username.equals(name)) username = name;
    }

    public synchronized void addApproved() {
        approvedContent++;
    }

    public synchronized void addSent() {
        sentContent++;
    }
}