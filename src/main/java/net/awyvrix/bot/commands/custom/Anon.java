package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;

public final class Anon implements Command {
    private final UserData data;
    private final TelegramClient client;

    public Anon(UserData data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/anon";
    }

    @Override
    public String description() {
        return "Переключить анонимность";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        User user = update.getMessage().getFrom();

        sendMessage(client, update,"Anonymous sending: " + data.switchTag(user.getId(), user.getUserName(), UserTag.ANONYMOUS));
    }
}