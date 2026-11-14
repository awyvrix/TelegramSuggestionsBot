package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.debug.NetworkMonitor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class Status implements Command {
    private final NetworkMonitor monitor;
    private final TelegramClient client;

    public Status(NetworkMonitor monitor, TelegramClient client) {
        this.monitor = monitor;
        this.client = client;
    }

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

        if (monitor.check(false)) {
            debug.append(" | ⚙ Status: ").append("[").append(status).append("]\n");
            debug.append(" | \uD83D\uDD2D Respond Time: ").append(monitor.latency).append("ms\n");
            sendMessage(client, update, debug.toString());
        } else {
            debug.append(" | \uD83D\uDD2D Respond Time: ").append("Cannot reach Telegram API service");
            sendMessage(client, update, debug.toString());
        }
    }
}