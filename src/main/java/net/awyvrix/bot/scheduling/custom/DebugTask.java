package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public class DebugTask implements ScheduledTask {
    int period = config.TEST_NETWORK;

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
        Main.networkMonitor.check(false);
    }
}