package net.awyvrix.bot.commands.custom.attachments;

import net.awyvrix.bot.Util;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.BotState;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.NetworkMonitor;
import net.awyvrix.bot.media.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

import static net.awyvrix.bot.Util.getMediaHash;
import static net.awyvrix.bot.data.config.ConfigManager.config;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;

public final class MediaProcessor implements Command {
    private final DataManager data;
    private final ModerationService service;
    private final Publisher publisher;
    private final TelegramClient client;
    private final NetworkMonitor monitor;

    public MediaProcessor(DataManager dataManager, ModerationService service, Publisher publisher, NetworkMonitor monitor, TelegramClient client) {
        this.data = dataManager;
        this.service = service;
        this.publisher = publisher;
        this.client = client;
        this.monitor = monitor;
    }

    @Override
    public String id() {
        return "#media";
    }

    @Override
    public String description() {
        return "Отправить контент";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.ATTACHMENT);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        synchronized (BotState.LOCK) {
            process(update);
        }
    }

    private void process(Update update) throws TelegramApiException  {
        if (update == null || !update.hasMessage()) return;
        Message message = update.getMessage();

        if (message.getFrom() == null) return;
        String author = message.getFrom().getUserName();
        long authorId = message.getFrom().getId();
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        Media media = MediaParser.parse(client, update);

        if (media == null) {
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4E");
            return;
        }
        data.increaseReceivedMedia();;

        User user = data.users().getOrCreate(authorId, author);
        boolean anon = data.users().getTag(authorId, author, UserTag.ANONYMOUS);
        Submission submission = new Submission(authorId, message.getChatId(), messageId, author, anon, Boolean.TRUE.equals(message.getHasMediaSpoiler()), media);
        user.updateUsername(author);

        String hash = getMediaHash(client, update, monitor, media);

        if (Math.floor(user.rating()) + config.BASIC_ACTIONS <= 0) {
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(client, update, "❌ Вы были перманентно забанены за свои деяния [м2]");
            return;

        } if (config.TEST_MODE) {
            Util.sendMessage(client, update, "⚠ Бот находится в тестовом режиме. Контент может быть не отправлен. Попробуйте позже");

        } if (user.actions() <= 0) {
            data.increaseSpamBlocked();
            user.increaseRating(-config.SPAM_FINE);
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(client, update, "❌ Контент не был отправлен: Слишком частые запросы [м2]");
            return;

        } if (data.sentContent().contains(media.uniqueId()) || data.sentHashedContent().contains(hash)) {
            data.increaseRepeatBlocked();
            user.increaseRating(-config.REPEAT_FINE);
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(client, update, "❌ Контент не был отправлен: \uD83E\uDE97 [м0]");
            return;

        } if (data.onModContent().contains(media.uniqueId())) {
            data.increaseSpamBlocked();
            user.increaseRating(-config.MOD_SPAM_FINE);
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(client, update, "❌ Контент не был отправлен: Уже на модерации [м1]");
            return;

        } if (data.users().getTag(authorId, author, UserTag.ADMIN)) {
            publisher.submit(submission, submission.isSpoiler());
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC4D");
            Util.sendMessage(client, update, "⚙️ Контент принудительно отправлен");

            if (hash != null) data.sentHashedContent().addToSent(hash);
            user.addSent();

        } else {
            long submissionId = NEXT_ID.incrementAndGet();
            service.send(media, submissionId);

            data.onModContent().addToSent(media.uniqueId());
            data.submissions().put(submissionId, submission);
            data.increaseAppliedContent();

            if (hash != null) data.sentHashedContent().addToSent(hash);
            user.addSent();
            user.increaseActions(-1);
            Util.replyEmoji(client, chatId, messageId, "\uD83D\uDC40");
            Util.sendMessage(client, update, "\uD83D\uDCE5 Контент отправлен на модерацию");
        }
    }
}