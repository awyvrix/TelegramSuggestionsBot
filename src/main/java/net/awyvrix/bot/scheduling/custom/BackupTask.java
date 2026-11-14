package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public class BackupTask implements ScheduledTask {
    private final DataManager dataManager;

    public BackupTask(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public String id() {
        return "backup";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.HOURS, config.BACKUP_PERIOD);
    }

    @Override
    public void execute() {
        try {
            dataManager.backup("persistent");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}