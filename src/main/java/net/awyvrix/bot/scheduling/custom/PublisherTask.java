package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.media.Submission;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.Main.MAINTENANCE;
import static net.awyvrix.bot.data.config.ConfigManager.config;
import static net.awyvrix.bot.media.Publisher.queue;

public class PublisherTask implements ScheduledTask {
    int period = config.TEST_MODE ? config.TEST_PUBLICATION_PERIOD : config.PUBLICATION_PERIOD;

    @Override
    public String id() {
        return "publisher";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.SECONDS, period);
    }

    @Override
    public void execute() {
        if (queue.isEmpty()) return;
        if (MAINTENANCE) return;
        Submission submission = queue.peek();

        if (submission == null) return;
        try {
            Main.publisher.publish();
            Main.dataManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}