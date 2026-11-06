package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkReport;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Report implements Command {

    @Override
    public String id() {
        return "/report";
    }

    @Override
    public String description() {
        return "Полный отчёт о работе";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(update, UserTag.DEVELOPER)) {
            StringBuilder debug = new StringBuilder("Report:\n" + " | TotalErrors: " + Main.networkMonitor.networkReports.size() + "\n | \n");
            debug.append("StartTime:EndTime - Duration");
            for (NetworkReport report : Main.networkMonitor.networkReports) {
                debug.append(" | ").append(report.startTime()).append(":").append(report.endTime()).append(" - ")
                        .append(report.duration()).append("ms")
                        .append(" (").append(report.duration() / 1000).append("s)\n");
            }
            sendMessage(update, debug.toString());
        }
    }
}