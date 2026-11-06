package net.awyvrix.bot.data.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.awyvrix.bot.data.BotData;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.util.DataSnapshot;
import net.awyvrix.bot.media.Submission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.awyvrix.bot.Main.*;
import static net.awyvrix.bot.data.user.UserData.users;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.PublicationScheduler.queue;

public final class BackupDataManager {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH.mm.ss");
    private static final DateTimeFormatter directoryFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ObjectMapper MAPPER = new ObjectMapper();

    public void backup() throws IOException {
        LocalDateTime date = LocalDateTime.now();
        Path backup = Path.of("backup");
        Path backupDay = Path.of("backup/" + date.format(directoryFormatter));

        if (!Files.exists(backup)) Files.createDirectory(backup);
        if (!Files.exists(backupDay)) Files.createDirectory(backupDay);
        Path FILE = Path.of(backupDay + "/" + date.format(formatter) + ".json");

        Map<Long, User> usersCopy = new HashMap<>(users);
        List<Submission> queueCopy = new ArrayList<>(queue);

        List<String> sentCopy = new ArrayList<>(sentContent.sent());
        List<String> publishedCopy = new ArrayList<>(publishedContent.sent());
        List<String> moderationCopy = new ArrayList<>(onModContent.sent());
        List<String> hashCopy = new ArrayList<>(sentHashedContent.sent());

        DataSnapshot snapshot = new DataSnapshot();
        snapshot.users.putAll(usersCopy);
        snapshot.queue.addAll(queueCopy);
        snapshot.hashed.addAll(hashCopy);

        snapshot.sent.addAll(sentCopy);
        snapshot.published.addAll(publishedCopy);
        snapshot.onMod.addAll(moderationCopy);

        snapshot.sentContent = BotData.sentContent;
        snapshot.appliedContent = BotData.appliedContent;;
        snapshot.approvedContent = BotData.approvedContent;;

        snapshot.repeatBlocked = BotData.repeatBlocked;
        snapshot.spamBlocked = BotData.spamBlocked;

        snapshot.nextSubmissionId = NEXT_ID.longValue();

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(FILE.toFile(), snapshot);
    }
}