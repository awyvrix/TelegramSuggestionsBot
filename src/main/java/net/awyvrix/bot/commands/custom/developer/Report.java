package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkReport;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Main.networkMonitor;
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
            StringBuilder debug = new StringBuilder("Report:\n" + " | TotalErrors: " + networkMonitor.networkReports.size() + "\n | \n");
            for (int i = 0; i < networkMonitor.networkReports.size(); i++) {
                debug.append(format(networkMonitor.networkReports.get(i) , i));
            }
            sendMessage(update, debug.toString());
        }
    }

    private String format(NetworkReport report, int n) {
        return " |  Downtime #" + n
                + "\n  | From: " + report.startTime()
                + "\n  | To: " + report.endTime()
                + "\n  | Duration: " + report.duration() + "ms" + " (" + report.duration()/1000 + "s)"
                + "\n -----\n";
    }
}