package net.awyvrix.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ArgumentCommand extends Command {
    void execute(Update update, Argument argument) throws TelegramApiException;
}