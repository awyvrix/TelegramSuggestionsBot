package net.awyvrix.bot;

import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.exceptions.ErrorHandler;
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
    public static boolean executeRights(TelegramClient client, UserData data, Update update, UserTag... tags) {
        User user = update.getMessage().getFrom();
        for (UserTag tag : tags) {
            if (data.getTag(user.getId(), user.getUserName(), tag)) return true;
        }

        try {
            client.execute(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("❌ Insufficient rights [0]")
                    .build());
        } catch (Exception e) {
            ErrorHandler.report("Failed to send message on execute rights", e);
        }
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

    public static void sendMessage(TelegramClient client, Update update, String message) {
        try {
            client.execute(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(message)
                    .build());
        } catch (Exception e) {
            ErrorHandler.report("Failed to send message", e, "msg", message);
        }
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
            ErrorHandler.report("Connection timed out due hash request", e);
            return null;
        }
        String url = "https://api.telegram.org/file/bot" + StaticConfigManager.config.TOKEN + "/" + file.getFilePath();

        try (var in = java.net.URI.create(url).toURL().openStream()) {
            return sha256(in.readAllBytes());
        } catch (Exception e) {
            ErrorHandler.report("Failed to get file hash", e);
            sendMessage(client, update, "❌ Не удалось обработать контент. Попробуйте позже");
            Util.replyEmoji(client, update.getMessage().getChatId(), update.getMessage().getMessageId(), "⚠");
        }
        return null;
    }

    public static void replyEmoji(TelegramClient client, long chatId, int messageId, String emoji) {
        SetMessageReaction reaction = SetMessageReaction.builder()
                .chatId(chatId)
                .messageId(messageId)
                .reactionTypes(List.of(new ReactionTypeEmoji(emoji)))
                .build();

        try {
            client.execute(reaction);
        } catch (Exception e) {
            ErrorHandler.report("Failed to reply emoji", e,
                    "chatId", chatId,
                    "msgId", messageId,
                    "emoji", emoji);
        }
    }
}