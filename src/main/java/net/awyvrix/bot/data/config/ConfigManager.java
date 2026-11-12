package net.awyvrix.bot.data.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.awyvrix.bot.Main.LOGGER;

public class ConfigManager {
    private static final Path FILE = Path.of("config.json");
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static Config config = new Config();

    public static void save() throws IOException {
        MAPPER.writeValue(FILE.toFile(), config);
    }

    public static void load() throws IOException {
        if (Files.exists(FILE)) {
            config = MAPPER.readValue(FILE.toFile(), Config.class);
            MAPPER.readerForUpdating(config).readValue(FILE.toFile());
            LOGGER.info("Config loaded");

            if (!checkValues()) throw new IllegalStateException("None of entries can be less or equals 0");
            save();
        } else {
            LOGGER.info("Config not found - Creating new");
            save();
        }
    }

    private static boolean checkValues() {
        if (config.BACKUP_PERIOD <= 0) return false;

        if (config.TEST_NETWORK <= 0) return false;
        if (config.TEST_PUBLICATION_PERIOD <= 0) return false;
        if (config.TEST_USER_PERIOD <= 0) return false;
        if (config.MAX_NETWORK_REPORTS <= 0) return false;

        if (config.PUBLICATION_PERIOD <= 0) return false;
        if (config.USER_PERIOD <= 0) return false;

        if (config.MAX_ACTIONS <= 0) return false;
        if (config.MAX_RECOVERY <= 0) return false;

        if (config.BASIC_ACTIONS <= 0) return false;
        if (config.BASIC_RECOVERY <= 0) return false;

        if (config.ACTIONS_DELTA <= 0) return false;

        if (config.SPAM_FINE <= 0) return false;
        if (config.REPEAT_FINE <= 0) return false;
        if (config.MOD_SPAM_FINE <= 0) return false;
        if (config.ACCEPT_BONUS <= 0) return false;
        return true;
    }
}