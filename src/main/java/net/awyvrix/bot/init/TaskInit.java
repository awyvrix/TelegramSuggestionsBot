package net.awyvrix.bot.init;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.scheduling.TaskScheduler;
import net.awyvrix.bot.scheduling.custom.BackupTask;
import net.awyvrix.bot.scheduling.custom.DebugTask;
import net.awyvrix.bot.scheduling.custom.PublisherTask;
import net.awyvrix.bot.scheduling.custom.UpdateUserTask;

import static net.awyvrix.bot.Main.LOGGER;

public final class TaskInit {
    private static final TaskScheduler registry = Main.taskScheduler;

    static public void initialise() {
        registry.register(new BackupTask());
        registry.register(new DebugTask());
        registry.register(new PublisherTask());
        registry.register(new UpdateUserTask());

        registry.setInitialised();
        LOGGER.info("All tasks registered");
    }
}