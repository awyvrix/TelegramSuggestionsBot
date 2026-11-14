package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.util.Set;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Save implements Command {
    private final DataManager data;
    private final TelegramClient client;

    public Save(DataManager data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/save";
    }

    @Override
    public String description() {
        return "Принудительное сохранение";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(client, data.users(), update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            try {
                data.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Forced Save executed\n  |Actor: {}", update.getMessage().getFrom().getUserName());
            sendMessage(client, update,"✅ Successfully saved");
        }
    }
}