package net.awyvrix.bot;

import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.media.MediaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Main extends DefaultLongPollingUpdateConsumer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static boolean MAINTENANCE = false;

    private final Bot bot;

    public Main(String token) {
        try {
            this.bot = new Bot(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bot.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bot.networkMonitor().check(true);

        if (bot.networkMonitor().lastSuccess != 0) LOGGER.info("All systems online");
        else LOGGER.error("Cannot connect to Telegram API");
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                bot.moderationHandler().handle(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (!update.hasMessage()) return;
        String key = MediaParser.hasMedia(update) ? "#media" : update.getMessage().getText();

        if (!bot.commandManager().commands().containsKey(key)) return;
        try {
            bot.commandManager().commands().get(key).execute(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        StaticConfigManager.load();
        String token = StaticConfigManager.config.TOKEN;

        try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
            application.registerBot(token, new Main(token));
            Thread.currentThread().join();
        }
    }
}