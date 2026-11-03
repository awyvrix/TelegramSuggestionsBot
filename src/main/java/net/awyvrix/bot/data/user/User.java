package net.awyvrix.bot.data.user;

public final class User {
    public String username = "///unspecified";
    public boolean isAnonymous = false;
    public boolean isAdmin = false;
    public boolean isDeveloper = false;
    public int sentContent = 0;
    public int approvedContent = 0;
    public float rating = 0;
    public int actions = 6;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public boolean setAnonymous() {
        isAnonymous = !isAnonymous;
        return isAnonymous;
    }

    public void updateUsername(String name) {
        if (username == null) username = name;
        if (!username.equals(name)) username = name;
    }

    public void setAdmin(boolean flag) {
        isAdmin = flag;
    }

    public void setDeveloper(boolean flag) {
        isDeveloper = flag;
    }

    public void addApproved() {
        approvedContent++;
    }

    public void addSent() {
        sentContent++;
    }
}