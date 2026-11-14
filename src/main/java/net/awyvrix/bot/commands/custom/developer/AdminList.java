package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class AdminList implements Command {
    private final DataManager data;
    private final TelegramClient client;

    public AdminList(DataManager data, TelegramClient client) {
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/adminlist";
    }

    @Override
    public String description() {
        return "Список админов";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        StringBuilder admins = new StringBuilder("Список админов:\n");
        StringBuilder developers = new StringBuilder("Список разработчиков:\n");
        List<User> userTop = new ArrayList<>(data.users().users().values());

        if (executeRights(client, data.users(), update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            for (int i = 0; i < data.userCount(); i++) {
                User user = userTop.get(i);
                String username = user.getTag(UserTag.ANONYMOUS) ? "[A] " + user.username() : user.username();

                if (user.getTag(UserTag.ADMIN)) admins.append(" | ").append(username).append("\n");
                if (user.getTag(UserTag.DEVELOPER)) developers.append(" | ").append(username).append("\n");
            }

            sendMessage(client, update,admins + "---\n" + developers);
        }
    }
}