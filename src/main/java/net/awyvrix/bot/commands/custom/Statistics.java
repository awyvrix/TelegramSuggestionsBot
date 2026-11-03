package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.Util.sendMessage;
import static net.awyvrix.bot.data.BotData.*;

public final class Statistics implements Command {

    @Override
    public String id() {
        return "/statistics";
    }

    @Override
    public String description() {
        return "Статистика бота";
    }

    @Override
    public CommandType type() {
        return CommandType.DEFAULT;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        sendMessage(client, update,"Статистика:\n" +
                " | Отправлено контента: " + sentContent + "\n" +
                " | Отправлено на модерацию: " + appliedContent + "\n" +
                " | Одобрено контента: " + approvedContent + "\n" +
                " |\n" +
                " | Заблокировано спама: " + spamBlocked + "\n" +
                " | Заблокировано баянов: " + repeatBlocked + "\n" +
                " |\n" +
                " | Уникальных пользователей: " + UserData.users.size() + "\n");
    }
}