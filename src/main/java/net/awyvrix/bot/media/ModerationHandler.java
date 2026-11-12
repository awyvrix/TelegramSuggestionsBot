package net.awyvrix.bot.media;

import net.awyvrix.bot.Util;
import net.awyvrix.bot.data.BotState;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static net.awyvrix.bot.Main.client;
import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class ModerationHandler {
    public static final AtomicLong NEXT_ID = new AtomicLong();
    public static final Map<Long, Submission> submissions = new ConcurrentHashMap<>();

    private final Publisher publisher;

    public ModerationHandler(Publisher publisher) {
        this.publisher = publisher;
    }

    public void handle(CallbackQuery query) throws TelegramApiException {
        if (!Util.checkRights(query.getFrom(), UserTag.ADMIN)) return;
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
        synchronized (BotState.LOCK) {
            Submission submission = submissions.get(submissionId);
            deleteModerationMessage(query);
            submissions.remove(submissionId);

            if (submission == null) return;
            replyStatus(submission.chatId(), submission.messageId(), "\uD83D\uDC4E");
        }
    }

    private void approve(CallbackQuery query, long submissionId, boolean spoiler) throws TelegramApiException {
        synchronized (BotState.LOCK) {
            Submission submission = submissions.get(submissionId);

            if (submission == null) return;
            publisher.submit(submission, spoiler);

            deleteModerationMessage(query);
            submissions.remove(submissionId);

            replyStatus(submission.chatId(), submission.messageId(), "\uD83D\uDC4D");
            User user = UserData.get(submission.authorId());

            if (user != null) user.rating += config.ACCEPT_BONUS;
        }
    }

    private void deleteModerationMessage(CallbackQuery query) throws TelegramApiException {
        client.execute(DeleteMessage.builder()
                .chatId(query.getMessage().getChatId())
                .messageId(query.getMessage().getMessageId())
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