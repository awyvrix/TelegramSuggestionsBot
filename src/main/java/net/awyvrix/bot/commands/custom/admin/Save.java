package net.awyvrix.bot.commands.custom.admin;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Save implements Command {

    @Override
    public String id() {
        return "/save";
    }

    @Override
    public String description() {
        return "Принудительное сохранение";
    }

    @Override
    public CommandType type() {
        return CommandType.DEVELOPER;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        if (executeRights(client, update)) {
            try {
                Main.dataManager.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Forced Save executed\n  |Actor: {}", update.getMessage().getFrom().getUserName());
            sendMessage(client, update,"✅ Successfully saved");
        }
    }
}