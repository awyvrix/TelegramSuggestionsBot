package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        if (executeRights(update, UserTag.DEVELOPER)) {
            StringBuilder debug = new StringBuilder("Debug:\n" + " | Version: " + VERSION + "\n" + " | Save Version: " + SAVE_VERSION + "\n");
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

                    .append(" | Queue: ").append(queue.size())

                    .append("\n---\nNetwork:")
                    .append("\n | LastSuccess: ").append(Main.networkMonitor.lastSuccess / 1_000_000)
                    .append("\n | Failures: ").append(Main.networkMonitor.totalFailures)
                    .append("\n | Hash Failures: ").append(Main.networkMonitor.hashFailures)
                    .append("\n | Latency: ").append(Main.networkMonitor.latency).append("ms")
                    .append("\n | Downtime: ").append(Main.networkMonitor.downtime).append("ms")
                        .append(" (").append(Main.networkMonitor.downtime/1000).append("s)")
                    .append("\n | LongestDowntime: ").append(Main.networkMonitor.longestDowntime)
                        .append("ms").append(" (").append(Main.networkMonitor.longestDowntime/1000).append("s)")
                    .append("\n | NextTask Delta: ").append((Main.taskScheduler.delta()) / 1_000_000)
                        .append("ms").append(" (").append((Math.round((float) Main.taskScheduler.delta() / 1_000_000_000))).append("s)")
                    .append("\n | Completed Tasks: ").append((Main.taskScheduler.completed));

            sendMessage(update, debug.toString());
        }
    }
}