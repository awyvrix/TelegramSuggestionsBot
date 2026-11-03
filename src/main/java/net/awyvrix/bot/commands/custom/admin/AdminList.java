package net.awyvrix.bot.commands.custom.admin;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class AdminList implements Command {

    @Override
    public String id() {
        return "/adminlist";
    }

    @Override
    public String description() {
        return "Список админов";
    }

    @Override
    public CommandType type() {
        return CommandType.DEVELOPER;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        StringBuilder admins = new StringBuilder("Список админов:\n");
        StringBuilder developers = new StringBuilder("Список разработчиков:\n");
        List<User> userTop = new ArrayList<>(UserData.users.values());

        if (executeRights(client, update)) {
            for (int i = 0; i < UserData.users.size(); i++) {
                User user = userTop.get(i);

                if (user.isAdmin) admins.append(" | ").append(user.isAnonymous ? "[A] " + user.username : user.username).append("\n");
                if (user.isDeveloper) developers.append(" | ").append(user.isAnonymous ? "[A] " + user.username : user.username).append("\n");
            }

            sendMessage(client, update,admins + "---\n" + developers);
        }
    }
}