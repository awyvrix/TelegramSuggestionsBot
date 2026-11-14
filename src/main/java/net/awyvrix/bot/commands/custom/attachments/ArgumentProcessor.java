package net.awyvrix.bot.commands.custom.attachments;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

public final class ArgumentProcessor implements Command {

    @Override
    public String id() {
        return "#arg-";
    }

    @Override
    public String description() {
        return "Аргумент к команде";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.ATTACHMENT, CommandType.UNLISTED);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        synchronized (BotState.LOCK) {
            process(update);
        }
    }

    private void process(Update update) throws TelegramApiException  {
        if (update == null || !update.hasMessage()) return;
        Message message = update.getMessage();

        if (message.getFrom() == null) return;

    }
}