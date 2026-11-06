package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class Status implements Command {

    @Override
    public String id() {
        return "/status";
    }

    @Override
    public String description() {
        return "Статус бота";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        StringBuilder debug = new StringBuilder("Check successful:\n");
        String status = Main.MAINTENANCE ? "Bot is currently under maintenance \uD83D\uDEE0\uFE0F" : "All systems online";
        if (config.TEST_MODE) status += ". Bot is launched in test mode \uD83D\uDEE0\uFE0F";

        Main.networkMonitor.check(false);
        debug.append(" | ⚙ Status: ").append("[").append(status).append("]\n");
        debug.append(" | \uD83D\uDD2D Respond Time: ").append(Main.networkMonitor.latency).append("ms\n");
        sendMessage(update,debug.toString());
    }
}