package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public class BackupTask implements ScheduledTask {
    int period = config.BACKUP_PERIOD;

    @Override
    public String id() {
        return "backup";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.HOURS, period);
    }

    @Override
    public void execute() {
        try {
            Main.backupDataManager.backup("persistent");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}