package net.awyvrix.bot.commands.custom.temp;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;

public final class GetLog implements Command {

    @Override
    public String id() {
        return "/getLog";
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
        User user = update.getMessage().getFrom();

        sendMessage(update,"Anonymous sending: " + Main.data.switchTag(user.getId(), user.getUserName(), UserTag.ANONYMOUS));
    }
}