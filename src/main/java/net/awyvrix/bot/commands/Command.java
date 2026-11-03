package net.awyvrix.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface Command {
    String id();
    String description();
    CommandType type();
    void execute(Update update, TelegramClient client) throws TelegramApiException;
}