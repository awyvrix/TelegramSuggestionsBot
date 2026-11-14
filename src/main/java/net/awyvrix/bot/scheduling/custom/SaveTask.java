package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class SaveTask implements ScheduledTask {
    private final DataManager dataManager;

    public SaveTask(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public String id() {
        return "save";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.SECONDS, 60);
    }

    @Override
    public void execute() {
        try {
            dataManager.save();
        } catch (IOException e) {
            throw new RuntimeException("Periodic data save failed", e);
        }
    }
}
