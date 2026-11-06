package net.awyvrix.bot;

import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.config.StaticConfigManager;
import net.awyvrix.bot.media.Media;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public class Util {
    public static boolean executeRights(TelegramClient client, Update update) throws TelegramApiException {
        User user = update.getMessage().getFrom();
        if (Main.data.getDeveloper(user.getId()) || Main.data.getAdmin(user.getId())) {
            return true;
        } else {
            client.execute(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("❌ Insufficient rights [0]")
                    .build());
        }
        return false;
    }

    public static boolean checkRights(User user, CommandType type) {
        if (type.equals(CommandType.ADMIN)) Main.data.getAdmin(user.getId());
        if (type.equals(CommandType.ADMIN)) Main.data.getDeveloper(user.getId());
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

    public static String getMediaHash(TelegramClient client, Media media) throws Exception {
        var file = client.execute(GetFile.builder().fileId(media.fileId()).build());
        String url = "https://api.telegram.org/file/bot" + StaticConfigManager.config.TOKEN + "/" + file.getFilePath();

        try (var in = java.net.URI.create(url).toURL().openStream()) {
            return sha256(in.readAllBytes());
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