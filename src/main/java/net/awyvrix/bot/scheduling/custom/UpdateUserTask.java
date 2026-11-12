package net.awyvrix.bot.scheduling.custom;

import net.awyvrix.bot.data.BotState;
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
        return "update_user";
    }

    @Override
    public Duration duration() {
        return new Duration(TimeUnit.SECONDS, period);
    }

    @Override
    public void execute() {
        synchronized (BotState.LOCK) {
            for (User user : UserData.users.values()) {
                int level = (int) (Math.floor(user.rating) / config.ACTIONS_DELTA);
                int recovery = Math.min(config.MAX_RECOVERY, config.BASIC_RECOVERY + level);
                int maxActions = Math.min(config.MAX_ACTIONS, config.BASIC_ACTIONS + level);
                user.actions = Math.min(maxActions, user.actions + recovery);
            }
        }
    }
}