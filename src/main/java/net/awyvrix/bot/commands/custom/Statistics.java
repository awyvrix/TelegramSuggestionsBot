package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.DataManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;

public final class Statistics implements Command {
    private final DataManager data;
    private final TelegramClient client;

    public Statistics(DataManager data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/statistics";
    }

    @Override
    public String description() {
        return "Статистика бота";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        sendMessage(client, update,"Статистика:\n" +
                " | Отправлено контента: " + data.receivedMediaCount() + "\n" +
                " | Отправлено на модерацию: " + data.appliedContent() + "\n" +
                " | Одобрено контента: " + data.approvedContent() + "\n" +
                " |\n" +
                " | Заблокировано спама: " + data.spamBlocked() + "\n" +
                " | Заблокировано баянов: " + data.repeatBlocked() + "\n" +
                " |\n" +
                " | Уникальных пользователей: " + data.users().users().size() + "\n");
    }
}