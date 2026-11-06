package net.awyvrix.bot.data.backup;

import net.awyvrix.bot.Main;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class BackupScheduler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        int period = config.BACKUP_PERIOD;
        LOGGER.info("Started BackupScheduler with period: {} {} ", period, TimeUnit.HOURS.name());
        scheduler.scheduleAtFixedRate(this::backup, 0, period, TimeUnit.HOURS);
    }

    private void backup() {
        try {
            Main.backupDataManager.backup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        scheduler.shutdown();
        LOGGER.info("UserProcessor Stopped");
    }
}