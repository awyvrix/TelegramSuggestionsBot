package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Set;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Backup implements Command {

    @Override
    public String id() {
        return "/backup";
    }

    @Override
    public String description() {
        return "Принудительная резервная копия";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(update, UserTag.DEVELOPER)) {
            try {
                Main.backupDataManager.backup();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Forced Backup executed\n  |Actor: {}", update.getMessage().getFrom().getUserName());
            sendMessage(update,"✅ Backup successfully completed");
        }
    }
}