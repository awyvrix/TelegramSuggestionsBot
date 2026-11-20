package net.awyvrix.bot;

import net.awyvrix.bot.commands.Argument;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.exceptions.ErrorHandler;
import net.awyvrix.bot.media.MediaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main extends DefaultLongPollingUpdateConsumer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static boolean MAINTENANCE = false;

    private final Bot bot;

    public Main(String token) {
        bot = new Bot(token);
        bot.start();
        bot.networkMonitor().check(true);

        if (bot.networkMonitor().lastSuccess != 0) LOGGER.info("All systems online");
        else LOGGER.error("Cannot connect to Telegram API");

        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            ErrorHandler.report("Uncaught exception (thread=" + thread.getName() + ")", error);
        });
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                bot.moderationHandler().handle(update);
            } catch (Exception e) {
                ErrorHandler.report("Failed to process submission action", e);
            }
        }

        if (!update.hasMessage()) return;
        CommandManager manager = bot.commandManager();
        Map<String, Command> commands = manager.commands();
        Map<Long, Argument> argumentCommands = manager.argumentCommands();
        long userId = update.getMessage().getFrom().getId();
        String key = MediaParser.hasMedia(update) ? "#media" : update.getMessage().getText();

        if (argumentCommands.containsKey(userId)) {
            try {
                argumentCommands.put(userId, argumentCommands.get(userId).addAll(splitString(update.getMessage().getText())));
                Argument argument = argumentCommands.get(userId);
                argumentCommands.remove(userId).command().execute(update, argument);
            } catch (Exception e) {
                ErrorHandler.report("Failed to process argument command", e,
                        "userId", userId,
                        "arguments", argumentCommands.get(userId).arguments());
            }
        }
        if (!commands.containsKey(key)) return;
        try {
            commands.get(key).execute(update);
        } catch (Exception e) {
            ErrorHandler.report("Failed to process command", e, "key", key);
        }
    }

    public static void main(String[] args) throws Exception {
        StaticConfigManager.load();
        String token = StaticConfigManager.config.TOKEN;

        try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
            application.registerBot(token, new Main(token));
            Thread.currentThread().join();
        } catch (Exception e) {
            ErrorHandler.report("Failed to create application", e);
        }
    }

    public static List<String> splitString(String text) {
        return Arrays.asList(text.trim().split("\\s+"));
    }
}