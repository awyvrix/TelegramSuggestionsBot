package net.awyvrix.bot.init;

import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.media.Publisher;
import net.awyvrix.bot.scheduling.TaskScheduler;
import net.awyvrix.bot.scheduling.custom.BackupTask;
import net.awyvrix.bot.scheduling.custom.DebugTask;
import net.awyvrix.bot.scheduling.custom.PublisherTask;
import net.awyvrix.bot.scheduling.custom.UpdateUserTask;

import static net.awyvrix.bot.Main.LOGGER;

public final class TaskInit {
    public static void initialise(TaskScheduler registry, DataManager dataManager, NetworkMonitor monitor, Publisher publisher) {
        registry.register(new BackupTask(dataManager));
        registry.register(new DebugTask(monitor));
        registry.register(new PublisherTask(publisher, dataManager));
        registry.register(new UpdateUserTask(dataManager));

        registry.setInitialised();
        LOGGER.info("All tasks registered");
    }
}