package net.awyvrix.bot;

import net.awyvrix.bot.commands.CommandRegistry;
import net.awyvrix.bot.data.*;
import net.awyvrix.bot.data.backup.BackupDataManager;
import net.awyvrix.bot.data.backup.BackupScheduler;
import net.awyvrix.bot.data.config.ConfigManager;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserUpdateScheduler;
import net.awyvrix.bot.data.util.DataSnapshot;
import net.awyvrix.bot.data.util.SetSnapshot;
import net.awyvrix.bot.debug.DebugScheduler;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.init.AdminsInit;
import net.awyvrix.bot.init.CommandsInit;
import net.awyvrix.bot.init.DevelopersInit;
import net.awyvrix.bot.init.MediaParser;
import net.awyvrix.bot.media.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

import static net.awyvrix.bot.data.user.UserData.users;
import static net.awyvrix.bot.data.config.ConfigManager.config;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.PublicationScheduler.queue;

public class Main implements LongPollingSingleThreadUpdateConsumer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static NetworkMonitor networkMonitor;
    public static boolean MAINTENANCE = false;

    public static final DebugScheduler debugScheduler = new DebugScheduler();
    public static final PublicationScheduler publicationScheduler = new PublicationScheduler();
    public static final BackupScheduler backupScheduler = new BackupScheduler();
    public static final UserUpdateScheduler userUpdateScheduler = new UserUpdateScheduler();

    public static final CommandRegistry commandRegistry = new CommandRegistry();
    public static final SetSnapshot sentContent = new SetSnapshot();
    public static final SetSnapshot sentHashedContent = new SetSnapshot();
    public static final SetSnapshot onModContent = new SetSnapshot();
    public static final SetSnapshot publishedContent = new SetSnapshot();

    //123
    public static final UserData data = new UserData();
    public static final DataManager dataManager = new DataManager();
    public static final BackupDataManager backupDataManager = new BackupDataManager();

    public final TelegramClient client;
    public static ModerationService service;
    public static Publisher publisher;
    public static ModerationHandler handler;

    public Main(String token) throws IOException {
        LOGGER.info("Starting bot...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down...");
            try {
                Main.dataManager.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            debugScheduler.stop();
            publicationScheduler.stop();
            userUpdateScheduler.stop();
            backupScheduler.stop();
        }));

        ConfigManager.load();
        CommandsInit.initialise();
        client = new OkHttpTelegramClient(token);
        service = new ModerationService(client, StaticConfigManager.config.MODERATOR);
        publisher = new Publisher(client, config.TEST_MODE ? StaticConfigManager.config.MODERATOR : StaticConfigManager.config.CHANNEL);
        handler = new ModerationHandler(client, publisher);
        networkMonitor = new NetworkMonitor(client);

        DataSnapshot snapshot = dataManager.load();
        users.putAll(snapshot.users);
        queue.addAll(snapshot.queue);

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

        publicationScheduler.start();
        userUpdateScheduler.start();
        debugScheduler.start();
        backupScheduler.start();
        LOGGER.info("Bot started");

        if (config.TEST_MODE) LOGGER.warn("Bot is launched it Test mode");
        networkMonitor.check(true);
        dataManager.setLaunched();
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
            commandRegistry.commands().get(key).execute(update, client);
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

    private void updateData() {
        int appliedContent = 0;
        int approvedContent = 0;

        if (BotData.sentContent != 0 || users.isEmpty()) return;
        LOGGER.info("Recovering Data...");
        for (User user : users.values()) {
            appliedContent += user.sentContent;
            approvedContent += user.approvedContent;
        }

        BotData.sentContent = appliedContent + approvedContent;
        BotData.appliedContent = appliedContent;
        BotData.approvedContent = approvedContent;
    }
}