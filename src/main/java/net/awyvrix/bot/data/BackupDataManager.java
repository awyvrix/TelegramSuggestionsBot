package net.awyvrix.bot.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class BackupDataManager {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH.mm.ss");
    private static final DateTimeFormatter directoryFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ObjectMapper MAPPER = new ObjectMapper();
    private boolean executed = false;

    public void backup(String type) throws IOException {
        if (executed) return;
        executed = true;

        try {
            LocalDateTime date = LocalDateTime.now();
            Path backup = Path.of("backup");
            Path backupDay = Path.of("backup/" + date.format(directoryFormatter));

            if (!Files.exists(backup)) Files.createDirectory(backup);
            if (!Files.exists(backupDay)) Files.createDirectory(backupDay);
            Path FILE = Path.of(backupDay + "/" + date.format(formatter) + "-" + type + ".json");

            MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValue(FILE.toFile(), DataManager.createSnapshot());
        } finally {
            executed = false;
        }
    }
}