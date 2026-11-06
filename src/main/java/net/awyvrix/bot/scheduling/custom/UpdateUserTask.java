package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.scheduling.Duration;
import net.awyvrix.bot.scheduling.ScheduledTask;

import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public class UpdateUserTask implements ScheduledTask {
    int period = config.TEST_MODE ? config.TEST_USER_PERIOD : config.USER_PERIOD;

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
        for (User user : UserData.users.values()) {
            int max = (int) (Math.floor(user.rating) + config.BASIC_ACTIONS);
            int maxRecovery = (1 + (int) (Math.floor(user.rating) / config.ACTIONS_DELTA));
            if (user.actions < Math.min(max, config.MAX_ACTIONS)) user.actions += Math.min(maxRecovery, config.MAX_RECOVERY);
        }
    }
}