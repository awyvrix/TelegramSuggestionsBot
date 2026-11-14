package net.awyvrix.bot.data.util;

import net.awyvrix.bot.Metadata;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.media.Submission;

import java.util.*;

public final class DataSnapshot {
    public int saveVersion = Metadata.SAVE_VERSION;
    public Set<String> sent = new HashSet<>();
    public Set<String> onMod = new HashSet<>();
    public Set<String> published = new HashSet<>();
    public Set<String> hashed = new HashSet<>();
    public Map<Long, User> users = new HashMap<>();
    public List<Submission> queue = new ArrayList<>();
    public Map<Long, Submission> submissions = new HashMap<>();
    public long nextSubmissionId = 1;

    public int repeatBlocked = 0;
    public int spamBlocked = 0;

    public int receivedMediaCount = 0;
    public int appliedContent = 0;
    public int approvedContent = 0;

    public void normalize() {
        if (sent == null) sent = new HashSet<>();
        if (onMod == null) onMod = new HashSet<>();
        if (published == null) published = new HashSet<>();
        if (hashed == null) hashed = new HashSet<>();
        if (users == null) users = new HashMap<>();
        if (queue == null) queue = new ArrayList<>();
        if (submissions == null) submissions = new HashMap<>();
        if (nextSubmissionId < 1) nextSubmissionId = 1;
    }
}