package net.awyvrix.bot.media;

import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static net.awyvrix.bot.Main.client;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class ModerationHandler {
    public static final AtomicLong NEXT_ID = new AtomicLong();
    public static final Map<Long, Submission> submissions = new HashMap<>();

    public static Publisher publisher;

    public ModerationHandler(Publisher publisher) {
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
        Submission submission = submissions.get(submissionId);
        submissions.remove(submissionId);
        deleteModerationMessage(query);
        answer(query, "Отклонено");
        if (submission.chatId() != 0) replyStatus(submission.authorId(), submission.chatId(), "\uD83D\uDC4E");
    }

    private void approve(CallbackQuery query, long submissionId, boolean spoiler) throws TelegramApiException {
        Submission submission = submissions.get(submissionId);

        try {
            publisher.submit(submission, spoiler);
            deleteModerationMessage(query);
            answer(query, spoiler ? "Опубликовано со спойлером" : "Опубликовано");
            if (submission.chatId() != 0) replyStatus(submission.authorId(), submission.chatId(), "\uD83D\uDC4D");

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

    private void replyStatus(long chatId, int messageId, String emoji) throws TelegramApiException {
        SetMessageReaction reaction = SetMessageReaction.builder()
                .chatId(chatId)
                .messageId(messageId)
                .reactionTypes(List.of(new ReactionTypeEmoji(emoji)))
                .build();

        client.execute(reaction);
    }
}