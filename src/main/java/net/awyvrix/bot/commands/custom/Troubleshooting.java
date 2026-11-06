package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;

public final class Troubleshooting implements Command {

    @Override
    public String id() {
        return "/troubleshooting";
    }

    @Override
    public String description() {
        return "Возможные ошибки";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        sendMessage(update,"""
                [0] Insufficient rights - Недостаточно прав
                [м0] \uD83E\uDE97 - Контент уже был в группе
                [м1] Уже на модерации - Контент уже на модерации
                [м2] Слишком частые запросы - Вы, возможно спамер
                [м3] Перманентный бан - Свяжитесь с админом, если это ошибка
                """);
    }
}