package net.awyvrix.bot.commands.custom;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.awyvrix.bot.Util.sendMessage;

public final class Leaderboard implements Command {

    @Override
    public String id() {
        return "/leaderboard";
    }

    @Override
    public String description() {
        return "Статистика участников";
    }

    @Override
    public CommandType type() {
        return CommandType.DEFAULT;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        User thisUser = UserData.getOrCreate(update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName());
        thisUser.updateUsername(update.getMessage().getFrom().getUserName());

        List<User> userTop = new ArrayList<>(UserData.users.values());
        userTop.sort(Comparator.comparingInt((User user) -> user.approvedContent).reversed());
        StringBuilder users = new StringBuilder("""
                Топ 10:
                 [Одобрено | отправлено]
                """);

        for (int i = 0; i < Math.min(10, userTop.size()); i++) {
            User user = userTop.get(i);

            users.append(" ").append(i + 1).append(": ")
                    .append(user.isAnonymous ? "Anonymous" : user.username)
                    .append(" [").append(user.approvedContent).append(" | ").append(user.sentContent).append("]\n");
        }

        sendMessage(client, update, users + "---\n"
                        + (thisUser.isAnonymous ? ("[A] " + thisUser.username) : thisUser.username)
                        + "  [" + thisUser.approvedContent + " | " + thisUser.sentContent + "]");
    }
}