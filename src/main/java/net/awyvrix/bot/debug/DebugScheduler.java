package net.awyvrix.bot.debug;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.media.Submission;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Metadata.TIME_UNIT;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class DebugScheduler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final Queue<Submission> queue = new ConcurrentLinkedQueue<>();

    public void start() {
        int period = config.TEST_NETWORK;
        LOGGER.info("Started DebugScheduler with period: {} {} ", period, TIME_UNIT.name());
        scheduler.scheduleAtFixedRate(this::check, 0, period, TIME_UNIT);
    }

    private void check() {
        Main.networkMonitor.check(false);
    }

    public void stop() {
        scheduler.shutdown();
        LOGGER.info("DebugScheduler Stopped");
    }
}