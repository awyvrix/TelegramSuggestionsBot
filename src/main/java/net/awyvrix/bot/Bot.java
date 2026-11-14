package net.awyvrix.bot;

import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.console.ConsoleCommandManager;
import net.awyvrix.bot.console.ConsoleManager;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.SaveManager;
import net.awyvrix.bot.data.config.ConfigManager;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.init.CommandsInit;
import net.awyvrix.bot.init.ConsoleCommandsInit;
import net.awyvrix.bot.init.TaskInit;
import net.awyvrix.bot.media.ModerationHandler;
import net.awyvrix.bot.media.ModerationService;
import net.awyvrix.bot.media.Publisher;
import net.awyvrix.bot.scheduling.ProcessorThread;
import net.awyvrix.bot.scheduling.TaskScheduler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public class Bot {
    private final TelegramClient client;
    private final ModerationService moderationService;
    private final Publisher publisher;
    private final ModerationHandler moderationHandler;
    private final NetworkMonitor networkMonitor;

    private final BotContext context;
    private final TaskScheduler taskScheduler;
    private final ProcessorThread processorThread;

    private final ConsoleManager consoleManager;

    public Bot(String token) {
        this.context = new BotContext();

        this.client = new OkHttpTelegramClient(token);
        this.moderationService = new ModerationService(client, StaticConfigManager.config.MODERATOR);
        this.publisher = new Publisher(client, config.TEST_MODE ? StaticConfigManager.config.MODERATOR : StaticConfigManager.config.CHANNEL, context.botDataManager());

        this.networkMonitor = new NetworkMonitor(client);
        this.taskScheduler = new TaskScheduler();
        this.processorThread = new ProcessorThread(taskScheduler);
        this.moderationHandler = new ModerationHandler(context.botDataManager(), publisher, client);

        this.consoleManager = new ConsoleManager(context.consoleCommandManager());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down...");
            processorThread.shutdown();
            try {
                context.botDataManager().save();
                context.botDataManager().backup("shutdown");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void start() throws IOException {
        LOGGER.info("Starting bot... [{}]", StaticConfigManager.config.NAME);

        ConfigManager.load();
        CommandsInit.initialise(client, context, context.commandManager(), context.botDataManager(),
                taskScheduler, networkMonitor, moderationService, publisher);

        context.botDataManager().load();

        TaskInit.initialise(taskScheduler, dataManager(), networkMonitor, publisher);
        processorThread.start();
        LOGGER.info("Bot started");

        if (config.TEST_MODE) LOGGER.warn("Bot is launched it Test mode");
        networkMonitor.check(true);

        ConsoleCommandsInit.initialise(context.consoleCommandManager());
        consoleManager.start();
    }

    public TelegramClient client() {
        return client;
    }

    public ModerationService moderationService() {
        return moderationService;
    }

    public Publisher publisher() {
        return publisher;
    }

    public NetworkMonitor networkMonitor() {
        return networkMonitor;
    }

    public ModerationHandler moderationHandler() {
        return moderationHandler;
    }

    public CommandManager commandManager() {
        return context.commandManager();
    }

    public ConsoleCommandManager consoleCommandManager() {
        return context.consoleCommandManager();
    }

    public SaveManager saveManager() {
        return context.saveManager();
    }

    public DataManager dataManager() {
        return context.botDataManager();
    }
}