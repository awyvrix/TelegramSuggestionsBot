package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.config.StaticConfigManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

import static net.awyvrix.bot.Util.sendMessage;

public final class Start implements Command {
    private StringBuilder commandList = null;
    private StringBuilder attachmentsList = null;
    private StringBuilder adminList = null;
    private StringBuilder developerList = null;

    @Override
    public String id() {
        return "/start";
    }

    @Override
    public String description() {
        return "Стартовое меню";
    }

    @Override
    public CommandType type() {
        return CommandType.DEFAULT;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        if (commandList == null || attachmentsList == null) build();
        sendMessage(client, update,"Это бот-предложка " + StaticConfigManager.config.NAME + " [" + StaticConfigManager.config.TAG + "]\n " +
                        "Список команд:\n" + commandList +
                        "\n Админ-команды:\n" + developerList +
                        "\n Список дополнений:\n" + attachmentsList +
                        "\nДля отправки дополнения требуется просто отправить требуемый тип медиа (картинка / видео). " +
                        "Бот поддерживает отправку нескольких медиа за раз. Никаких команд не требуется.\n\n - Bot is developed by Awyvrix");
    }

    public void build() {
        Map<String, Command> registry = Main.commandRegistry.commands();
        commandList = new StringBuilder();
        attachmentsList = new StringBuilder();
        adminList = new StringBuilder();
        developerList = new StringBuilder();

        for (Command command : registry.values()) {
            if (command.type().equals(CommandType.UNLISTED)) continue;
            String cmd = "  | " + command.id() + " - " + command.description() + "\n";

            if (command.id().charAt(0) == '#') attachmentsList.append(cmd);
            else if (command.type().equals(CommandType.ADMIN)) adminList.append(cmd);
            else if (command.type().equals(CommandType.DEVELOPER)) developerList.append(cmd);
            else commandList.append(cmd);
        }
    }
}