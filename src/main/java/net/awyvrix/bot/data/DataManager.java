package net.awyvrix.bot.data;

import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.util.DataSnapshot;
import net.awyvrix.bot.data.util.SetSnapshot;
import net.awyvrix.bot.init.AdminsInit;
import net.awyvrix.bot.init.DevelopersInit;
import net.awyvrix.bot.media.Submission;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.Publisher.queue;

public final class DataManager {
    private final SaveManager saveManager;
    private final UserData users = new UserData();
    private final Map<Long, Submission> submissions = new ConcurrentHashMap<>();

    private int receivedMediaCount = 0; // sentContent
    private int appliedContent = 0; // appliedContent
    private int approvedContent = 0; // approvedContent
    private int repeatBlocked = 0; // repeatBlocked
    private int spamBlocked = 0; // spamBlocked

    private final SetSnapshot sentContent = new SetSnapshot(); // sentContent
    private final SetSnapshot sentHashedContent = new SetSnapshot(); // sentHashedContent
    private final SetSnapshot onModContent = new SetSnapshot(); // onModContent
    private final SetSnapshot publishedContent = new SetSnapshot(); // publishedContent

    public DataManager(SaveManager saveManager) {
        this.saveManager = saveManager;
    }

    private void loadData() throws IOException {
        DataSnapshot snapshot = saveManager.load();
        users.users().putAll(snapshot.users);
        queue.addAll(snapshot.queue);
        submissions.putAll(snapshot.submissions);

        sentContent.sent().addAll(snapshot.sent);
        publishedContent.sent().addAll(snapshot.published);

        onModContent.sent().addAll(snapshot.onMod);
        sentHashedContent.sent().addAll(snapshot.hashed);

        receivedMediaCount = snapshot.receivedMediaCount;
        appliedContent = snapshot.appliedContent;
        approvedContent = snapshot.approvedContent;
        repeatBlocked = snapshot.repeatBlocked;
        spamBlocked = snapshot.spamBlocked;

        NEXT_ID.set(snapshot.nextSubmissionId);

        updateData();
        AdminsInit.initialise(users);
        DevelopersInit.initialise(users);
        saveManager.setLaunched();
    }

    private void updateData() {
        int applied = 0;
        int approved = 0;

        if (receivedMediaCount != 0 || users.users().isEmpty()) return;
        LOGGER.info("Recovering Data...");
        for (User user : users.users().values()) {
            applied += user.submittedMediaCount();
            approved += user.approvedContentCount();
        }

        receivedMediaCount = applied;
        appliedContent = applied;
        approvedContent = approved;
    }

    public void backup(String type) throws IOException {
        saveManager.backup(createSnapshot(), type);
    }

    public void save() throws IOException {
        saveManager.save(createSnapshot());
    }

    public void load() throws IOException {
        loadData();
    }

    private DataSnapshot createSnapshot() {
        synchronized (BotState.LOCK) {
            DataSnapshot snapshot = new DataSnapshot();
            users.users().forEach((id, user) -> snapshot.users.put(id, user.copy()));
            snapshot.queue.addAll(queue);
            snapshot.submissions.putAll(submissions());
            snapshot.sent.addAll(sentContent.copy());
            snapshot.published.addAll(publishedContent.copy());
            snapshot.onMod.addAll(onModContent.copy());
            snapshot.hashed.addAll(sentHashedContent.copy());
            snapshot.receivedMediaCount = receivedMediaCount;
            snapshot.appliedContent = appliedContent;
            snapshot.approvedContent = approvedContent;
            snapshot.repeatBlocked = repeatBlocked;
            snapshot.spamBlocked = spamBlocked;
            snapshot.nextSubmissionId = NEXT_ID.get();
            return snapshot;
        }
    }

    public SetSnapshot sentContent() {
        return sentContent;
    }

    public SetSnapshot sentHashedContent() {
        return sentHashedContent;
    }

    public SetSnapshot onModContent() {
        return onModContent;
    }

    public SetSnapshot publishedContent() {
        return publishedContent;
    }

    public Map<Long, Submission> submissions() {
        return submissions;
    }

    public int receivedMediaCount() {
        return receivedMediaCount;
    }

    public int appliedContent() {
        return appliedContent;
    }

    public int approvedContent() {
        return approvedContent;
    }

    public int repeatBlocked() {
        return repeatBlocked;
    }

    public int spamBlocked() {
        return spamBlocked;
    }

    public void increaseReceivedMedia() {
        receivedMediaCount++;
    }

    public void increaseAppliedContent() {
        appliedContent++;
    }

    public void increaseApprovedContent() {
        approvedContent++;
    }

    public void increaseRepeatBlocked() {
        repeatBlocked++;
    }

    public void increaseSpamBlocked() {
        spamBlocked++;
    }

    public UserData users() {
        return users;
    }

    public int userCount() {
        return users.users().size();
    }
}