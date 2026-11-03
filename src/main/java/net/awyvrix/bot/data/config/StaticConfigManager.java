package net.awyvrix.bot.data.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.awyvrix.bot.Main.LOGGER;

public class StaticConfigManager {
    private static final Path FILE = Path.of("bot.json");
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static StaticConfig config = new StaticConfig();

    public static void save() throws IOException {
        MAPPER.writeValue(FILE.toFile(), config);
    }

    public static void load() throws IOException {
        if (Files.exists(FILE)) {
            config = MAPPER.readValue(FILE.toFile(), StaticConfig.class);
            LOGGER.info("StaticConfig loaded");
        } else {
            LOGGER.info("StaticConfig not found - Creating new");
            LOGGER.error("StaticConfig must be redacted to continue loading");
            save();
            Runtime.getRuntime().exit(-2);
        }
    }
}