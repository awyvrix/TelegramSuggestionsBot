package net.awyvrix.bot.console.custom;

import net.awyvrix.bot.console.ConsoleCommand;
import net.awyvrix.bot.data.config.ConfigManager;

import java.io.IOException;

import static net.awyvrix.bot.Main.LOGGER;

public class ReloadConfigCommand implements ConsoleCommand {

    @Override
    public String name() {
        return "reloadcfg";
    }

    @Override
    public String description() {
        return "Reloads cfg";
    }

    @Override
    public void execute(String[] args) {
        LOGGER.info("Reloading config...");
        try {
            ConfigManager.load();
            LOGGER.info("Config successfully reloaded...");
        } catch (IOException e) {
            LOGGER.error("Error while reloading config...");
            throw new RuntimeException(e);
        }
    }
}