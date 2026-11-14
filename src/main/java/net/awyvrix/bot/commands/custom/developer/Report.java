package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.config.ConfigManager;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.debug.NetworkReport;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Set;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Report implements Command {
    private final TelegramClient client;
    private final NetworkMonitor monitor;
    private final UserData data;

    public Report(NetworkMonitor monitor, UserData data, TelegramClient client) {
        this.client = client;
        this.monitor = monitor;
        this.data = data;
    }

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
        if (executeRights(client, data, update, UserTag.DEVELOPER)) {
            List<NetworkReport> networkReports = monitor.networkReports;
            int count = networkReports.size();
            int c = 0;

            StringBuilder debug = new StringBuilder("Report:\n" + " | TotalErrors: " + count + "\n | \n");
            for (int i = count-1; i >= 0; i--) {
                debug.append(format(networkReports.get(i) , count-i));
                if (++c >= ConfigManager.config.MAX_NETWORK_REPORTS) break;
            }
            sendMessage(client, update, debug.toString());
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