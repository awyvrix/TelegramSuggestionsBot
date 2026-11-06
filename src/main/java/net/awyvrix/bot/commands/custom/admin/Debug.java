package net.awyvrix.bot.commands.custom.admin;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.media.PublicationScheduler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.Metadata.VERSION;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Debug implements Command {

    @Override
    public String id() {
        return "/debug";
    }

    @Override
    public String description() {
        return "Информация для разработчиков";
    }

    @Override
    public CommandType type() {
        return CommandType.DEVELOPER;
    }

    @Override
    public void execute(Update update, TelegramClient client) throws TelegramApiException {
        if (executeRights(client, update)) {
            StringBuilder debug = new StringBuilder("Debug:\n" + " | Version: " + VERSION + "\n");
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

            long uptimeMs = runtimeBean.getUptime();
            long days = TimeUnit.MILLISECONDS.toDays(uptimeMs);
            long hours = TimeUnit.MILLISECONDS.toHours(uptimeMs) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMs) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMs) % 60;
            Main.networkMonitor.check(true);

            debug.append(" | Uptime: ")
                    .append(days).append("d ")
                    .append(hours).append("h ")
                    .append(minutes).append("m ")
                    .append(seconds).append("s\n")

                    .append(" | Queue: ").append(PublicationScheduler.queue.size())

                    .append("\n---\nNetwork:")
                    .append("\n | LastSuccess: ").append(Main.networkMonitor.lastSuccess / 1_000_000)
                    .append("\n | Failures: ").append(Main.networkMonitor.totalFailures)
                    .append("\n | Hash Failures: ").append(Main.networkMonitor.hashFailures)
                    .append("\n | Latency: ").append(Main.networkMonitor.latency + "ms")
                    .append("\n | Downtime: ").append(Main.networkMonitor.downtime + "ms")
                    .append("\n | LongestDowntime: ").append(Main.networkMonitor.longestDowntime + "ms");

            sendMessage(client, update, debug.toString());
        }
    }
}