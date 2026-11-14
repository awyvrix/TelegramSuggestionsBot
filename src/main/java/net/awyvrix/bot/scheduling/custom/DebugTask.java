package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public class DebugTask implements ScheduledTask {
    private final NetworkMonitor monitor;

    public DebugTask(NetworkMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String id() {
        return "debugger";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.SECONDS, config.TEST_NETWORK);
    }

    @Override
    public void execute() {
        monitor.check(false);
    }
}