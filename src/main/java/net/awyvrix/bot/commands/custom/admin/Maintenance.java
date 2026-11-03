package net.awyvrix.bot.commands.custom.admin;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.MAINTENANCE;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Maintenance implements Command {

    @Override
    public String id() {
        return "/maintenance";
    }

    @Override
    public String description() {
        return "Тех-обслуживание";
    }

    @Override
    public CommandType type() {
        return CommandType.DEVELOPER;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        if (executeRights(client, update)) {
            MAINTENANCE = !MAINTENANCE;
            LOGGER.warn("Maintenance mode: {}\n  |Actor: {}", MAINTENANCE, update.getMessage().getFrom().getUserName());
            sendMessage(client, update, "\uD83D\uDEE0\uFE0F Maintenance mode: " + (MAINTENANCE ? "Enabled" : "Disabled"));
        }
    }
}