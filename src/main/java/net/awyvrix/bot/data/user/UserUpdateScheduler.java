package net.awyvrix.bot.data.user;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Metadata.TIME_UNIT;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class UserUpdateScheduler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        int period = config.TEST_MODE ? config.TEST_USER_PERIOD : config.USER_PERIOD;
        LOGGER.info("Started UserUpdateScheduler with period: {} {} ", period, TIME_UNIT.name());
        scheduler.scheduleAtFixedRate(this::publishNext, 0, period, TIME_UNIT);
    }

    private void publishNext() {
        for (User user : UserData.users.values()) {
            int max = (int) (Math.floor(user.rating) + config.BASIC_ACTIONS);
            int maxRecovery = (1 + (int) (Math.floor(user.rating) / config.ACTIONS_DELTA));
            if (user.actions < Math.min(max, config.MAX_ACTIONS)) user.actions += Math.min(maxRecovery, config.MAX_RECOVERY);
        }
    }

    public void stop() {
        scheduler.shutdown();
        LOGGER.info("UserUpdateScheduler Stopped");
    }
}