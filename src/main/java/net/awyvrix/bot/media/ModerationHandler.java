package net.awyvrix.bot.media;

import net.awyvrix.bot.Util;
import net.awyvrix.bot.data.BotState;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserTag;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static net.awyvrix.bot.data.config.ConfigManager.config;

public final class ModerationHandler {
    public static final AtomicLong NEXT_ID = new AtomicLong();

    private final DataManager data;
    private final Publisher publisher;
    private final TelegramClient client;

    public ModerationHandler(DataManager botDataManager, Publisher publisher, TelegramClient client) {
        this.data = botDataManager;
        this.publisher = publisher;
        this.client = client;
    }

    public void handle(Update update) throws TelegramApiException {
        CallbackQuery query = update.getCallbackQuery();

        if (!Util.checkRights(update, data.users(), UserTag.ADMIN)) return;
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
            Map<Long, Submission> submissions = data.submissions();
            Submission submission = submissions.get(submissionId);
            deleteModerationMessage(query);
            submissions.remove(submissionId);

            if (submission == null) return;
            replyStatus(submission.sourceChatId(), submission.sourceMessageId(), "\uD83D\uDC4E");
        }
    }

    private void approve(CallbackQuery query, long submissionId, boolean spoiler) throws TelegramApiException {
        synchronized (BotState.LOCK) {
            Map<Long, Submission> submissions = data.submissions();
            Submission submission = submissions.get(submissionId);

            if (submission == null) return;
            publisher.submit(submission, spoiler);

            deleteModerationMessage(query);
            submissions.remove(submissionId);

            replyStatus(submission.sourceChatId(), submission.sourceMessageId(), "\uD83D\uDC4D");
            User user = data.users().get(submission.authorId());

            if (user != null) user.increaseRating(config.ACCEPT_BONUS);
        }
    }

    private void deleteModerationMessage(CallbackQuery query) throws TelegramApiException {
        if (!config.KEEP_SUBMISSIONS) return;
        if (query.getMessage() == null) return;
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