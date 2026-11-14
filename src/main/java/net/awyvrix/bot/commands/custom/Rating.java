package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class Rating implements Command {
    private final UserData data;
    private final TelegramClient client;

    public Rating(UserData data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/rating";
    }

    @Override
    public String description() {
        return "Показывает ваш рейтинг";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        User u = update.getMessage().getFrom();
        net.awyvrix.bot.data.user.User user = data.getOrCreate(u.getId(), u.getUserName());
        int level = (int) (Math.floor(user.rating()) / config.ACTIONS_DELTA);
        int recovery = Math.min(config.MAX_RECOVERY, config.BASIC_RECOVERY + level);
        int maxActions = Math.min(config.MAX_ACTIONS, config.BASIC_ACTIONS + level);

        String res = "Сводка о " + user.username() +
                "\n | Level: " + level +
                "\n | Rating: " + user.rating() +
                "\n | Actions: " + user.actions() +
                "\n | Recovery: " + recovery +
                "\n | Max Actions: " + maxActions +
                "\nRecovery - сколько восстанавливается действий за раз" +
                "\nMax Actions - максимальное количество действий" +
                "\nActions - 1 отправка контента = 1 действие\n" +
                "\n⚙ Рейтинг увеличивается при одобрении контента модератором" +
                "\n⚠ Рейтинг используется исключительно анти-спам системой";

        sendMessage(client, update, res);
    }
}