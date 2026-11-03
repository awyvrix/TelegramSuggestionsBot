package net.awyvrix.bot.data.util;

import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.media.Submission;

import java.util.*;

public final class DataSnapshot {
    public Set<String> sent = new HashSet<>();
    public Set<String> onMod = new HashSet<>();
    public Set<String> published = new HashSet<>();
    public Set<String> hashed = new HashSet<>();
    public Map<Long, User> users = new HashMap<>();
    public List<Submission> queue = new ArrayList<>();
    public long nextSubmissionId = 1;

    public int repeatBlocked = 0;
    public int spamBlocked = 0;

    public int sentContent = 0;
    public int appliedContent = 0;
    public int approvedContent = 0;
}