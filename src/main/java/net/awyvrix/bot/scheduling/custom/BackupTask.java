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
        return "debugger";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.SECONDS, period);
    }

    @Override
    public void execute() {
        try {
            Main.backupDataManager.backup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}