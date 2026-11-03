package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.Util.sendMessage;

public final class Anon implements Command {

    @Override
    public String id() {
        return "/anon";
    }

    @Override
    public String description() {
        return "Переключить анонимность";
    }

    @Override
    public CommandType type() {
        return CommandType.DEFAULT;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        User user = update.getMessage().getFrom();

        sendMessage(client, update,"Anonymous sending: " + Main.data.switchAnon(user.getId(), user.getUserName()));
    }
}