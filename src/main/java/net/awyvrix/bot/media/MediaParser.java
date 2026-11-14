package net.awyvrix.bot.media;

import net.awyvrix.bot.Util;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class MediaParser {
    public static Media parse(TelegramClient client, Update update) throws TelegramApiException {
        if (!update.hasMessage()) return null;
        var message = update.getMessage();

        if (message.hasPhoto() && config.ALLOW_PHOTO) {
            var photo = message.getPhoto().getLast();
            if (photo.getFileSize() > config.MAX_PHOTO_SIZE * 1_048_576) {
                Util.sendMessage(client, update, format(photo.getFileSize() / 1_048_576));
                return null;
            }
            return new Media(photo.getFileId(), photo.getFileUniqueId(), Media.Type.PHOTO, message.getCaption());
        }

        if (message.hasVideo() && config.ALLOW_VIDEO) {
            var video = message.getVideo();
            if (video.getFileSize() > config.MAX_VIDEO_SIZE * 1_048_576L) {
                Util.sendMessage(client, update, format(video.getFileSize() / 1_048_576));
                return null;
            }
            return new Media(video.getFileId(), video.getFileUniqueId(), Media.Type.VIDEO, message.getCaption());
        }

        if (message.hasDocument() && config.ALLOW_DOCUMENT) {
            var document = message.getDocument();
            if (document.getFileSize() > config.MAX_DOCUMENT_SIZE * 1_048_576L) {
                Util.sendMessage(client, update, format(document.getFileSize() / 1_048_576));
                return null;
            }
            return new Media(document.getFileId(), document.getFileUniqueId(), Media.Type.DOCUMENT, message.getCaption());
        }

        return null;
    }

    public static boolean hasMedia(Update update) {
        var message = update.getMessage();
        return message.hasPhoto() || message.hasVideo() || message.hasDocument();
    }

    private static String format(long size) {
        return "❌ File size exceeds max amount: " + size + "mb > " + config.MAX_PHOTO_SIZE + "mb [м4]";
    }
}