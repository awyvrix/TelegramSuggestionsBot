package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.Set;

import static net.awyvrix.bot.Util.checkRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Start implements Command {
    private StringBuilder commandList = null;
    private StringBuilder attachmentsList = null;
    private StringBuilder adminList = null;
    private StringBuilder developerList = null;
    private boolean built = false;

    private final CommandManager commandManager;
    private final UserData data;
    private final TelegramClient client;

    public Start(CommandManager commandManager, UserData data, TelegramClient client) {
        this.commandManager = commandManager;
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/start";
    }

    @Override
    public String description() {
        return "Стартовое меню";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEFAULT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (!built) build();
        StringBuilder developerList = new StringBuilder();
        StringBuilder adminList = new StringBuilder();

        if (checkRights(update, data, UserTag.DEVELOPER)) developerList = this.developerList;
        if (checkRights(update, data, UserTag.ADMIN)) adminList = this.adminList;
        sendMessage(client, update,"Это бот-предложка " + StaticConfigManager.config.NAME + " [" + StaticConfigManager.config.TAG + "]\n " +
                        commandList +
                        adminList +
                        developerList +
                        attachmentsList +
                        "\nДля отправки дополнения требуется просто отправить требуемый тип медиа (картинка / видео). " +
                        "Бот поддерживает отправку нескольких медиа за раз. Никаких команд не требуется.\n\n - Bot is developed by Awyvrix");
    }

    public void build() {
        Map<String, Command> registry = commandManager.commands();
        commandList = new StringBuilder("Список команд:\n");
        attachmentsList = new StringBuilder("\n Список дополнений:\n");
        developerList = new StringBuilder("\n Команды разработчиков:\n");
        adminList = new StringBuilder("\n Команды админов:\n");

        for (Command command : registry.values()) {
            Set<CommandType> types = command.types();
            if (types.contains(CommandType.UNLISTED)) continue;
            String cmd = "  | " + command.id() + " - " + command.description() + "\n";

            if (types.contains(CommandType.DEFAULT)) commandList.append(cmd);
            if (types.contains(CommandType.ATTACHMENT)) attachmentsList.append(cmd);

            if (types.contains(CommandType.ADMIN)) adminList.append(cmd);
            if (types.contains(CommandType.DEVELOPER)) developerList.append(cmd);
        }
        built = true;
    }
}