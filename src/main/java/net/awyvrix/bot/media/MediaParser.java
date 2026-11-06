package net.awyvrix.bot.media;

import org.telegram.telegrambots.meta.api.objects.Update;

public final class MediaParser {
    public static Media parse(Update update) {
        if (!update.hasMessage()) return null;
        var message = update.getMessage();

        if (message.hasPhoto()) {
            var photo = message.getPhoto().getLast();
            return new Media(photo.getFileId(), photo.getFileUniqueId(), Media.Type.PHOTO, message.getCaption());
        }

        if (message.hasVideo()) {
            var video = message.getVideo();
            return new Media(video.getFileId(), video.getFileUniqueId(), Media.Type.VIDEO, message.getCaption());
        }

        if (message.hasDocument()) {
            var document = message.getDocument();
            return new Media(document.getFileId(), document.getFileUniqueId(), Media.Type.DOCUMENT, message.getCaption());
        }

        return null;
    }

    public static boolean hasMedia(Update update) {
        var message = update.getMessage();
        return message.hasPhoto() || message.hasVideo() || message.hasDocument();
    }
}