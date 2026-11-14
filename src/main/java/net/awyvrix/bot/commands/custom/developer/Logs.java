package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Logs implements Command {
    private final UserData data;
    private final TelegramClient client;

    public Logs(UserData data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/logs";
    }

    @Override
    public String description() {
        return "Получить логи";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {

        if (executeRights(client, data, update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            sendMessage(client, update,"text");
        }
    }
}