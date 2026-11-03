package net.awyvrix.bot.media;

import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class ModerationHandler {
    public static final AtomicLong NEXT_ID = new AtomicLong();
    public static final Map<Long, Submission> submissions = new HashMap<>();

    public final TelegramClient client;
    public static Publisher publisher;

    public ModerationHandler(TelegramClient client, Publisher publisher) {
        this.client = client;
        ModerationHandler.publisher = publisher;
    }

    public void handle(CallbackQuery query) throws TelegramApiException {
        String[] data = query.getData().split(":", 2);

        if (data.length != 2) return;
        long submissionId = Long.parseLong(data[1]);

        switch (data[0]) {
            case "reject" -> reject(query, submissionId);
            case "approve" -> approve(query, submissionId, false);
            case "spoiler" -> approve(query, submissionId, true);
        }
    }

    private void reject(CallbackQuery query, long submissionId) throws TelegramApiException {
        submissions.remove(submissionId);
        deleteModerationMessage(query);
        answer(query, "Отклонено");
    }

    private void approve(CallbackQuery query, long submissionId, boolean spoiler) throws TelegramApiException {
        Submission submission = submissions.get(submissionId);

        try {
            publisher.submit(submission, spoiler);
            deleteModerationMessage(query);
            answer(query, spoiler ? "Опубликовано со спойлером" : "Опубликовано");

            User user = UserData.get(submission.authorId());
            if (user != null) user.rating += config.ACCEPT_BONUS;
        } catch (TelegramApiException e) {
            answer(query, "Ошибка публикации");
            throw e;
        }
    }

    private void deleteModerationMessage(CallbackQuery query) throws TelegramApiException {
        client.execute(DeleteMessage.builder()
                .chatId(query.getMessage().getChatId())
                .messageId(query.getMessage().getMessageId())
                .build());
    }

    private void answer(CallbackQuery query, String text) throws TelegramApiException {
        client.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId())
                .text(text)
                .build());
    }
}