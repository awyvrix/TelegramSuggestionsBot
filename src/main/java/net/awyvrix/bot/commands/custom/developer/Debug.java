package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.scheduling.TaskScheduler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.awyvrix.bot.Metadata.SAVE_VERSION;
import static net.awyvrix.bot.Metadata.VERSION;
import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;
import static net.awyvrix.bot.media.Publisher.queue;

public final class Debug implements Command {
    private final TaskScheduler taskScheduler;
    private final NetworkMonitor networkMonitor;
    private final UserData data;
    private final TelegramClient client;

    public Debug(TaskScheduler taskScheduler, NetworkMonitor networkMonitor, UserData data, TelegramClient client) {
        this.taskScheduler = taskScheduler;
        this.networkMonitor = networkMonitor;
        this.client = client;
        this.data = data;
    }

    @Override
    public String id() {
        return "/debug";
    }

    @Override
    public String description() {
        return "Информация для разработчиков";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(client, data, update, UserTag.DEVELOPER)) {
            StringBuilder debug = new StringBuilder("Debug:\n" + " | Version: " + VERSION + "\n" + " | Save Version: " + SAVE_VERSION + "\n");
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

            long uptimeMs = runtimeBean.getUptime();
            long days = TimeUnit.MILLISECONDS.toDays(uptimeMs);
            long hours = TimeUnit.MILLISECONDS.toHours(uptimeMs) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMs) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMs) % 60;
            networkMonitor.check(true);

            debug.append(" | Uptime: ")
                    .append(days).append("d ")
                    .append(hours).append("h ")
                    .append(minutes).append("m ")
                    .append(seconds).append("s\n")

                    .append(" | Queue: ").append(queue.size())

                    .append("\n---\nNetwork:")
                    .append("\n | LastSuccess: ").append(networkMonitor.lastSuccess / 1_000_000)
                    .append("\n | Failures: ").append(networkMonitor.totalFailures)
                    .append("\n | Hash Failures: ").append(networkMonitor.hashFailures)
                    .append("\n | Latency: ").append(networkMonitor.latency).append("ms")
                    .append("\n | Downtime: ").append(networkMonitor.downtime).append("ms")
                        .append(" (").append(networkMonitor.downtime/1000).append("s)")
                    .append("\n | LongestDowntime: ").append(networkMonitor.longestDowntime)
                        .append("ms").append(" (").append(networkMonitor.longestDowntime/1000).append("s)")
                    .append("\n | NextTask Delta: ").append((taskScheduler.delta()) / 1_000_000)
                        .append("ms").append(" (").append((Math.round((float) taskScheduler.delta() / 1_000_000_000))).append("s)")
                    .append("\n | Completed Tasks: ").append((taskScheduler.completed()));

            sendMessage(client, update, debug.toString());
        }
    }
}