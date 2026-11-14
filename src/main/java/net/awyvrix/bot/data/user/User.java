package net.awyvrix.bot.data.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.HashSet;
import java.util.Set;

import static net.awyvrix.bot.data.config.ConfigManager.config;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public final class User {
    private String username;
    private final Set<UserTag> tags = new HashSet<>();
    private int submittedMediaCount = 0;
    private int approvedContentCount = 0;

    private float rating = 0;
    private int actions = config.BASIC_ACTIONS;

    public User() {}

    public User(String username) {
        if (username == null) username = "[unspecified]";
        this.username = username;
    }

    public synchronized User copy() {
        User copy = new User(username);
        copy.tags.addAll(tags);
        copy.submittedMediaCount = submittedMediaCount;
        copy.approvedContentCount = approvedContentCount;
        copy.rating = rating;
        copy.actions = actions;
        return copy;
    }

    public synchronized boolean switchTag(UserTag tag) {
        if (tags.contains(tag)) tags.remove(tag);
        else tags.add(tag);
        return tags.contains(tag);
    }

    public synchronized boolean getTag(UserTag tag) {
        return tags.contains(tag);
    }

    public synchronized void setTag(UserTag tag, boolean isPresent) {
        if (isPresent) tags.add(tag);
        else tags.remove(tag);
    }

    public synchronized void updateUsername(String name) {
        if (name == null) name = "[unspecified]";
        if (username == null) username = name;
        if (!username.equals(name)) username = name;
    }

    public synchronized void addSent() {
        submittedMediaCount++;
    }

    public synchronized void addApproved() {
        approvedContentCount++;
    }

    public synchronized void increaseRating(float val) {
        rating += val;
    }

    public synchronized int actions() {
        return actions;
    }

    public synchronized void increaseActions(int val) {
        actions += val;
    }

    public synchronized void setActions(int val) {
        actions = val;
    }

    public synchronized float rating() {
        return rating;
    }

    public synchronized int submittedMediaCount() {
        return submittedMediaCount;
    }

    public synchronized int approvedContentCount() {
        return approvedContentCount;
    }

    public synchronized String username() {
        return username;
    }
}