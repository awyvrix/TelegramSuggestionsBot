package net.awyvrix.bot.media;

import net.awyvrix.bot.Main;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.MAINTENANCE;
import static net.awyvrix.bot.Metadata.TIME_UNIT;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class PublicationScheduler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final Queue<Submission> queue = new ConcurrentLinkedQueue<>();

    public void start() {
        int period = config.TEST_MODE ? config.TEST_PUBLICATION_PERIOD : config.PUBLICATION_PERIOD;
        LOGGER.info("Started PublicationScheduler with period: {} {} ", period, TIME_UNIT.name());
        scheduler.scheduleAtFixedRate(this::publishNext, 0, period, TIME_UNIT);
    }

    private void publishNext() {
        if (queue.isEmpty()) return;
        if (MAINTENANCE) return;
        Submission submission = queue.peek();

        if (submission == null) return;
        try {
            Main.publisher.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        scheduler.shutdown();
        LOGGER.info("PublicationScheduler Stopped");
    }
}