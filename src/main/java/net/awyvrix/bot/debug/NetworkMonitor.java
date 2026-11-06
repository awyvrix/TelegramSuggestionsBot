package net.awyvrix.bot.debug;

import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.Main.LOGGER;

public final class NetworkMonitor {
    private final TelegramClient client;
    public volatile long lastSuccess;
    public volatile long latency;

    public volatile int failures;
    public volatile int totalFailures;
    public volatile int hashFailures;

    public volatile long downtime;
    public volatile long longestDowntime = Long.MIN_VALUE;

    private long downStart;

    public NetworkMonitor(TelegramClient client) {
        this.client = client;
    }

    public void check(boolean log) {
        long start = System.nanoTime();
        try {
            client.execute(new GetMe());
            latency = (System.nanoTime() - start) / 1_000_000;
            lastSuccess = System.currentTimeMillis();

            if (failures > 0) LOGGER.info("Telegram API connection restored: {}ms with {}attempts", latency, failures);
            if (log) LOGGER.info("Telegram API OK: {}ms", latency);
            if (latency > 10000) LOGGER.error("Telegram API Respond longer 10 000ms");
            if (downStart != 0) downtimeUpdate();
            failures = 0;
        } catch (Exception e) {
            downStart = System.currentTimeMillis();
            failures++;
            totalFailures++;
            LOGGER.error("Telegram API failure #{}: {}", failures, e.toString());
        }
    }

    private void downtimeUpdate() {
        long time = System.currentTimeMillis() - downStart;
        downtime += time;
        longestDowntime = Math.max(longestDowntime, time);
        downStart = 0;
    }
}