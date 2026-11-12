package net.awyvrix.bot;

import net.awyvrix.bot.commands.CommandRegistry;
import net.awyvrix.bot.console.ConsoleCommandManager;
import net.awyvrix.bot.console.ConsoleManager;
import net.awyvrix.bot.console.custom.HelpCommand;
import net.awyvrix.bot.console.custom.StopCommand;
import net.awyvrix.bot.data.BackupDataManager;
import net.awyvrix.bot.data.BotData;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.config.ConfigManager;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.util.DataSnapshot;
import net.awyvrix.bot.data.util.SetSnapshot;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.init.*;
import net.awyvrix.bot.media.*;
import net.awyvrix.bot.scheduling.ProcessorThread;
import net.awyvrix.bot.scheduling.TaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

import static net.awyvrix.bot.data.config.ConfigManager.config;
import static net.awyvrix.bot.data.user.UserData.users;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.ModerationHandler.submissions;
import static net.awyvrix.bot.media.Publisher.queue;

public class Main extends DefaultLongPollingUpdateConsumer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static NetworkMonitor networkMonitor;
    public static boolean MAINTENANCE = false;

    public static final TaskScheduler taskScheduler = new TaskScheduler();
    public static final ProcessorThread processorThread = new ProcessorThread(taskScheduler);
    public static final CommandRegistry commandRegistry = new CommandRegistry();
    private final ConsoleCommandManager commandManager = new ConsoleCommandManager();

    public static final SetSnapshot sentContent = new SetSnapshot();
    public static final SetSnapshot sentHashedContent = new SetSnapshot();
    public static final SetSnapshot onModContent = new SetSnapshot();
    public static final SetSnapshot publishedContent = new SetSnapshot();

    public static final UserData data = new UserData();
    public static final DataManager dataManager = new DataManager();
    public static final BackupDataManager backupDataManager = new BackupDataManager();

    public static TelegramClient client;
    public static ModerationService service;
    public static Publisher publisher;
    public static ModerationHandler handler;

    public Main(String token) throws IOException {
        LOGGER.info("Starting bot... [{}]", StaticConfigManager.config.NAME);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down...");
            processorThread.shutdown();
            try {
                Main.dataManager.save();
                Main.backupDataManager.backup("shutdown");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        ConfigManager.load();
        CommandsInit.initialise();

        client = new OkHttpTelegramClient(token);
        service = new ModerationService(StaticConfigManager.config.MODERATOR);
        publisher = new Publisher(config.TEST_MODE ? StaticConfigManager.config.MODERATOR : StaticConfigManager.config.CHANNEL);
        handler = new ModerationHandler(publisher);
        networkMonitor = new NetworkMonitor(client);
        loadData();

        processorThread.start();
        LOGGER.info("Bot started");

        if (config.TEST_MODE) LOGGER.warn("Bot is launched it Test mode");
        networkMonitor.check(true);

        ConsoleCommandsInit.initialise(commandManager);
        new ConsoleManager(commandManager).start();
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                handle(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (!update.hasMessage()) return;
        String key = MediaParser.hasMedia(update) ? "#media" : update.getMessage().getText();

        if (!commandRegistry.commands().containsKey(key)) return;
        try {
            commandRegistry.commands().get(key).execute(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        StaticConfigManager.load();
        String token = StaticConfigManager.config.TOKEN;

        try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
            application.registerBot(token, new Main(token));
            if (networkMonitor.lastSuccess != 0) LOGGER.info("All systems online");
            else LOGGER.error("Cannot connect to Telegram API");
            Thread.currentThread().join();
        }
    }

    public static void sendToModeration(Media media, long id) throws TelegramApiException {
        service.send(media, id);
    }

    public static void submit(Submission submission, boolean spoiler) {
        publisher.submit(submission, spoiler);
    }

    public static void handle(CallbackQuery query) throws TelegramApiException {
        handler.handle(query);
    }

    private void loadData() throws IOException {
        DataSnapshot snapshot = dataManager.load();
        users.putAll(snapshot.users);
        queue.addAll(snapshot.queue);
        submissions.putAll(snapshot.submissions);

        sentContent.sent().addAll(snapshot.sent);
        publishedContent.sent().addAll(snapshot.published);

        onModContent.sent().addAll(snapshot.onMod);
        sentHashedContent.sent().addAll(snapshot.hashed);

        BotData.sentContent = snapshot.sentContent;
        BotData.appliedContent = snapshot.appliedContent;
        BotData.approvedContent = snapshot.approvedContent;

        BotData.repeatBlocked = snapshot.repeatBlocked;
        BotData.spamBlocked = snapshot.spamBlocked;
        NEXT_ID.set(snapshot.nextSubmissionId);

        updateData();
        AdminsInit.initialise();
        DevelopersInit.initialise();
        TaskInit.initialise();

        dataManager.setLaunched();
    }

    private void updateData() {
        int appliedContent = 0;
        int approvedContent = 0;

        if (BotData.sentContent != 0 || users.isEmpty()) return;
        LOGGER.info("Recovering Data...");
        for (User user : users.values()) {
            appliedContent += user.sentContent;
            approvedContent += user.approvedContent;
        }

        BotData.sentContent = appliedContent;
        BotData.appliedContent = appliedContent;
        BotData.approvedContent = approvedContent;
    }
}