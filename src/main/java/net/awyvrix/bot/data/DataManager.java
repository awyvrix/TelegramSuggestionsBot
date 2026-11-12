package net.awyvrix.bot.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.awyvrix.bot.Metadata;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.util.DataSnapshot;
import net.awyvrix.bot.media.ModerationHandler;
import net.awyvrix.bot.media.Submission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.awyvrix.bot.Main.*;
import static net.awyvrix.bot.data.user.UserData.users;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.ModerationHandler.submissions;
import static net.awyvrix.bot.media.Publisher.queue;

public final class DataManager {
    private static final Path FILE = Path.of("data.json");
    private static final Path TEMP_FILE = Path.of("data.json.tmp");
    private final ObjectMapper MAPPER = new ObjectMapper();
    private volatile boolean launched;

    public synchronized void save() throws IOException {
        if (!launched) return;
        DataSnapshot snapshot = createSnapshot();
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(TEMP_FILE.toFile(), snapshot);
        try {
            Files.move(TEMP_FILE, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
            Files.move(TEMP_FILE, FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public DataSnapshot load() throws IOException {
        if (!Files.exists(FILE) || Files.size(FILE) == 0) {
            LOGGER.info("Bot data not found or empty - Creating new");
            return new DataSnapshot();
        }
        JsonNode root = MAPPER.readTree(FILE.toFile());
        int version = root.path("SAVE_VERSION").asInt(0);

        if (version > Metadata.SAVE_VERSION) throw new IOException("Save format " + version + " is newer than supported format " + Metadata.SAVE_VERSION);
        while (version < Metadata.SAVE_VERSION) {
            root = migrate(root, version);
            version++;
            ((ObjectNode) root).put("SAVE_VERSION", version);
            LOGGER.info("Migrated save data: {} -> {}", version - 1, version);
        }
        DataSnapshot snapshot = MAPPER.treeToValue(root, DataSnapshot.class);
        snapshot.normalize();
        return snapshot;
    }

    public void setLaunched() {
        launched = true;
    }

    public boolean isLaunched() {
        return launched;
    }

    private JsonNode migrate(JsonNode source, int fromVersion) throws IOException {
        ObjectNode target = source.isObject() ? ((ObjectNode) source).deepCopy() : MAPPER.createObjectNode();

        switch (fromVersion) {
            case 0 -> {
                target.put("SAVE_VERSION", 1);
            }
            case 1 -> {

            }
            default -> throw new IOException("No migration registered from save version " + fromVersion);
        }

        return target;
    }

    public static DataSnapshot createSnapshot() {
        synchronized (BotState.LOCK) {
            DataSnapshot snapshot = new DataSnapshot();
            users.forEach((id, user) -> snapshot.users.put(id, user.copy()));
            snapshot.queue.addAll(queue);
            snapshot.submissions.putAll(submissions);
            snapshot.sent.addAll(sentContent.copy());
            snapshot.published.addAll(publishedContent.copy());
            snapshot.onMod.addAll(onModContent.copy());
            snapshot.hashed.addAll(sentHashedContent.copy());
            snapshot.sentContent = BotData.sentContent;
            snapshot.appliedContent = BotData.appliedContent;
            snapshot.approvedContent = BotData.approvedContent;
            snapshot.repeatBlocked = BotData.repeatBlocked;
            snapshot.spamBlocked = BotData.spamBlocked;
            snapshot.nextSubmissionId = NEXT_ID.get();
            return snapshot;
        }
    }
}