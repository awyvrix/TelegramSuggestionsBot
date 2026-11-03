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
            LOGGER.info("Config loaded");
        } else {
            LOGGER.info("Config not found - Creating new");
            save();
        }
    }
}