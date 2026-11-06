package net.awyvrix.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

public interface Command {
    String id();
    String description();
    Set<CommandType> types();
    void execute(Update update) throws TelegramApiException;
}