package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Argument;
import net.awyvrix.bot.commands.ArgumentCommand;
import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.Language;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Set;

import static net.awyvrix.bot.Util.sendMessage;

public final class Lang implements ArgumentCommand {
    private final CommandManager commandManager;
    private final UserData data;
    private final TelegramClient client;

    public Lang(CommandManager commandManager, UserData data, TelegramClient client) {
        this.commandManager = commandManager;
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/lang";
    }

    @Override
    public String description() {
        return "Выбрать язык";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        sendMessage(client, update,"Please choose lang: ");
        commandManager.addArgument(update.getMessage().getFrom().getId(), new Argument(this, 0, List.of()));
    }

    @Override
    public void execute(Update update, Argument argument) throws TelegramApiException {
        User user = data.getOrCreate(update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName());

        switch (argument.arguments().getFirst().toLowerCase()) {
            case "ru" -> user.setLanguage(Language.RU);
            case "en" -> user.setLanguage(Language.EN);
            case "lolcat" -> user.setLanguage(Language.LOLCAT);
            default -> sendMessage(client, update,"Wrong language");
        }
    }
}