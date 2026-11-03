package net.awyvrix.bot.media;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public final class ModerationService {
    private final TelegramClient client;
    private final long moderationChatId;

    public ModerationService(TelegramClient client, long moderationChatId) {
        this.client = client;
        this.moderationChatId = moderationChatId;
    }

    public void send(Media media, long submissionId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("❌ Отклонить", "reject:" + submissionId),
                        button("✅ Одобрить", "approve:" + submissionId),
                        button("⚠️ Спойлер", "spoiler:" + submissionId)
                ))
                .build();

        switch (media.type()) {
            case PHOTO -> client.execute(SendPhoto.builder()
                    .chatId(moderationChatId)
                    .photo(new InputFile(media.fileId()))
                    .caption(media.caption())
                    .replyMarkup(keyboard)
                    .build());

            case VIDEO -> client.execute(SendVideo.builder()
                    .chatId(moderationChatId)
                    .video(new InputFile(media.fileId()))
                    .caption(media.caption())
                    .replyMarkup(keyboard)
                    .build());

            case DOCUMENT -> client.execute(SendDocument.builder()
                    .chatId(moderationChatId)
                    .document(new InputFile(media.fileId()))
                    .caption(media.caption())
                    .replyMarkup(keyboard)
                    .build());
        }
    }

    private InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(data)
                .build();
    }
}