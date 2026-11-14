package net.awyvrix.bot;

import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.media.Media;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static net.awyvrix.bot.Main.LOGGER;

public class Util {
    public static boolean executeRights(TelegramClient client, UserData data, Update update, UserTag... tags) throws TelegramApiException {
        User user = update.getMessage().getFrom();
        for (UserTag tag : tags) {
            if (data.getTag(user.getId(), user.getUserName(), tag)) return true;
        }

        client.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("❌ Insufficient rights [0]")
                .build());
        return false;
    }

    public static boolean checkRights(Update update, UserData data, UserTag... tags) {
        User user = update.getMessage().getFrom();
        for (UserTag tag : tags) {
            if (data.getTag(user.getId(), user.getUserName(), tag)) return true;
        }

        return false;
    }

    public static boolean checkRights(User user, UserData data, UserTag... tags) {
        for (UserTag tag : tags) {
            if (data.getTag(user.getId(), user.getUserName(), tag)) return true;
        }

        return false;
    }

    public static void sendMessage(TelegramClient client, Update update, String message) throws TelegramApiException {
        client.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .build());
    }

    public static String sha256(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getMediaHash(TelegramClient client, Update update, NetworkMonitor networkMonitor, Media media) {
        File file;
        try {
            file = client.execute(GetFile.builder().fileId(media.fileId()).build());
        } catch (TelegramApiException e) {
            networkMonitor.hashFailures++;
            LOGGER.error("Could not get file hash: connection timed out");
            return null;
        }
        String url = "https://api.telegram.org/file/bot" + StaticConfigManager.config.TOKEN + "/" + file.getFilePath();

        try (var in = java.net.URI.create(url).toURL().openStream()) {
            return sha256(in.readAllBytes());
        } catch (IOException e) {
            LOGGER.error("Cannot get file hash");
            try {
                sendMessage(client, update, "❌ Не удалось обработать контент. Попробуйте позже");
                Util.replyEmoji(client, update.getMessage().getChatId(), update.getMessage().getMessageId(), "⚠");
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException();
        }
    }

    public static void replyEmoji(TelegramClient client, long chatId, int messageId, String emoji) throws TelegramApiException {
        SetMessageReaction reaction = SetMessageReaction.builder()
                .chatId(chatId)
                .messageId(messageId)
                .reactionTypes(List.of(new ReactionTypeEmoji(emoji)))
                .build();

        client.execute(reaction);
    }
}