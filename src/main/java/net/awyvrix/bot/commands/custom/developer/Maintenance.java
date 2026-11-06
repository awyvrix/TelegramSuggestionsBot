package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

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
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER, CommandType.ADMIN);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            MAINTENANCE = !MAINTENANCE;
            LOGGER.warn("Maintenance mode: {}\n  |Actor: {}", MAINTENANCE, update.getMessage().getFrom().getUserName());
            sendMessage(update, "\uD83D\uDEE0\uFE0F Maintenance mode: " + (MAINTENANCE ? "Enabled" : "Disabled"));
        }
    }
}