package net.awyvrix.bot.init;

import net.awyvrix.bot.BotContext;
import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.commands.custom.*;
import net.awyvrix.bot.commands.custom.attachments.MediaProcessor;
import net.awyvrix.bot.commands.custom.developer.*;
import net.awyvrix.bot.commands.custom.fun.Gos;
import net.awyvrix.bot.commands.custom.fun.LoreReport;
import net.awyvrix.bot.commands.custom.fun.Number;
import net.awyvrix.bot.commands.custom.fun.Object;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.config.ConfigManager;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.media.ModerationService;
import net.awyvrix.bot.media.Publisher;
import net.awyvrix.bot.scheduling.TaskScheduler;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.Main.LOGGER;

public final class CommandsInit {
    public static void initialise(TelegramClient client, BotContext context, CommandManager registry, DataManager data,
                                  TaskScheduler taskScheduler, NetworkMonitor networkMonitor,
                                  ModerationService service, Publisher publisher) {
        registry.register(new Start(context.commandManager(), data.users(), client));
        registry.register(new Status(networkMonitor, client));
        registry.register(new Troubleshooting(client));

        registry.register(new Maintenance(data.users(), client));
        registry.register(new Save(context.botDataManager(), client));
        registry.register(new Backup(context.botDataManager(), client));
        registry.register(new AdminList(context.botDataManager(), client));
        registry.register(new Debug(taskScheduler, networkMonitor, data.users(), client));
        registry.register(new Report(networkMonitor, data.users(), client));

        registry.register(new Anon(context.botDataManager().users(), client));
        registry.register(new Statistics(context.botDataManager(), client));
        registry.register(new Leaderboard(context.botDataManager().users(), client));
        registry.register(new Rating(context.botDataManager().users(), client));

        registry.register(new MediaProcessor(data, service, publisher, networkMonitor, client));

        if (ConfigManager.config.FUN_MODE) {
            // These commands are just for fun and not included in source code
            LOGGER.warn("Fun mode activated");
            registry.register(new Gos(client));
            registry.register(new Number(client));
            registry.register(new Object(client));
            registry.register(new LoreReport(client));
        }

        LOGGER.info("All commands registered");
    }
}