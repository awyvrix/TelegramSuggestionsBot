package net.awyvrix.bot.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.awyvrix.bot.Metadata;
import net.awyvrix.bot.data.util.DataSnapshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.awyvrix.bot.Main.LOGGER;

public final class SaveManager {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH.mm.ss");
    private final DateTimeFormatter directoryFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Path FILE = Path.of("data.json");
    private final Path TEMP_FILE = Path.of("data.json.tmp");
    private final ObjectMapper MAPPER = new ObjectMapper();
    private volatile boolean launched;

    public synchronized void save(DataSnapshot snapshot) throws IOException {
        if (!launched) return;
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
        int version = root.path("saveVersion").asInt(0);

        if (version > Metadata.SAVE_VERSION) throw new IOException("Save format " + version + " is newer than supported format " + Metadata.SAVE_VERSION);
        while (version < Metadata.SAVE_VERSION) {
            root = migrate(root, version);
            version++;
            ((ObjectNode) root).put("saveVersion", version);
            LOGGER.info("Migrated save data: {} -> {}", version - 1, version);
        }
        System.out.println(root);
        DataSnapshot snapshot = MAPPER.treeToValue(root, DataSnapshot.class);
        snapshot.normalize();
        return snapshot;
    }

    public void backup(DataSnapshot snapshot, String type) throws IOException {
        LocalDateTime date = LocalDateTime.now();
        Path backup = Path.of("backup");
        Path backupDay = Path.of("backup/" + date.format(directoryFormatter));

        if (!Files.exists(backup)) Files.createDirectory(backup);
        if (!Files.exists(backupDay)) Files.createDirectory(backupDay);
        Path FILE = Path.of(backupDay + "/" + date.format(formatter) + "-" + type + ".json");

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(FILE.toFile(), snapshot);
    }

    private JsonNode migrate(JsonNode source, int fromVersion) throws IOException {
        ObjectNode target = source.isObject() ? ((ObjectNode) source).deepCopy() : MAPPER.createObjectNode();

        switch (fromVersion) {
            case 0 -> {
                target.put("saveVersion", 1);
            }
            case 1 -> {
                // Added new fields
            }
            case 2 -> {
                JsonNode users = target.get("users");
                if (users instanceof ObjectNode object) {
                    object.elements().forEachRemaining(user -> {
                        if (user instanceof ObjectNode u) migrateUser(u);
                    });
                }

                JsonNode submissions = target.get("submissions");
                if (submissions instanceof ObjectNode object) {
                    object.elements().forEachRemaining(submission -> {
                        if (submission instanceof ObjectNode s) migrateSubmission(s);
                    });
                }

                JsonNode queue = target.get("queue");
                if (queue instanceof ArrayNode array) {
                    for (JsonNode entry : array) {
                        if (entry instanceof ObjectNode q) migrateSubmission(q);
                    }
                }

                renameField(target, "sentContent", "receivedMediaCount");
            }
            default -> throw new IOException("No migration registered from save version " + fromVersion);
        }

        return target;
    }

    public void setLaunched() {
        launched = true;
    }

    public boolean isLaunched() {
        return launched;
    }

    private static void migrateUser(ObjectNode target) {
        renameField(target, "userTags", "tags");
        renameField(target, "sentContent", "submittedMediaCount");
        renameField(target, "approvedContent", "approvedContentCount");
    }

    private static void migrateSubmission(ObjectNode target) {
        System.out.println("migrate" + target);
        renameField(target, "chatId", "sourceChatId");
        renameField(target, "messageId", "sourceMessageId");
        renameField(target, "authorName", "authorDisplayName");
        renameField(target, "anonymous", "isAnonymous");
        renameField(target, "spoiler", "isSpoiler");
    }

    private static void renameField(ObjectNode target, String oldName, String newName) {
        if (!target.has(newName) && target.has(oldName)) {
            target.set(newName, target.remove(oldName));
        }
    }
}