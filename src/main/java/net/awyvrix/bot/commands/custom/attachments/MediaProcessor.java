package net.awyvrix.bot.commands.custom.attachments;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.Util;
import net.awyvrix.bot.commands.Command;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.BotData;
import net.awyvrix.bot.data.user.User;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.media.Media;
import net.awyvrix.bot.media.MediaParser;
import net.awyvrix.bot.media.Submission;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static net.awyvrix.bot.Main.*;
import static net.awyvrix.bot.Util.getMediaHash;
import static net.awyvrix.bot.data.config.ConfigManager.config;
import static net.awyvrix.bot.data.user.UserData.getOrCreate;
import static net.awyvrix.bot.media.ModerationHandler.NEXT_ID;
import static net.awyvrix.bot.media.ModerationHandler.submissions;

public final class MediaProcessor implements Command {

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
        Message message = update.getMessage();
        String author = message.getFrom().getUserName();
        long authorId = message.getFrom().getId();
        int messageId = message.getMessageId();
        Media media = MediaParser.parse(update);

        if (media == null) return;
        BotData.sentContent++;
        User user = getOrCreate(authorId, author);
        boolean anon = Main.data.getTag(authorId, author, UserTag.ANONYMOUS);
        Submission submission = new Submission(authorId, messageId, author, anon, false, media);
        user.updateUsername(author);

        String hash = null;
        try {
            hash = getMediaHash(media);
        } catch (Exception e) {
            LOGGER.error("Cannot get file hash", e);
            networkMonitor.hashFailures++;
        }

        if (Math.floor(user.rating) + config.BASIC_ACTIONS <= 0) {
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(update, "❌ Вы были перманентно забанены за свои деяния [м2]");
            return;

        } if (config.TEST_MODE) {
            Util.sendMessage(update, "⚠ Бот находится в тестовом режиме. Контент может быть не отправлен. Попробуйте позже");

        } if (user.actions <= 0) {
            BotData.spamBlocked++;
            user.rating -= config.SPAM_FINE;
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(update, "❌ Контент не был отправлен: Слишком частые запросы [м2]");
            return;

        } if (sentContent.contains(media.uniqueId()) || sentHashedContent.contains(hash)) {
            BotData.repeatBlocked++;
            user.rating -= config.REPEAT_FINE;
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(update, "❌ Контент не был отправлен: \uD83E\uDE97 [м0]");
            return;

        } if (onModContent.contains(media.uniqueId())) {
            BotData.spamBlocked++;
            user.rating -= config.MOD_SPAM_FINE;
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC4E");
            Util.sendMessage(update, "❌ Контент не был отправлен: Уже на модерации [м1]");
            return;

        } if (Main.data.getTag(authorId, author, UserTag.ADMIN)) {
            Main.submit(submission, false);
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC4D");
            Util.sendMessage(update, "⚙\uFE0F Контент принудительно отправлен");
            user.addSent();

        } else {
            BotData.appliedContent++;
            long submissionId = NEXT_ID.incrementAndGet();
            submissions.put(submissionId, submission);

            Main.sendToModeration(media, submissionId);
            onModContent.addToSent(media.uniqueId());
            sentHashedContent.addToSent(hash);

            user.addSent();
            user.actions--;
            Util.replyEmoji(authorId, messageId, "\uD83D\uDC40");
            Util.sendMessage(update, "\uD83D\uDCE5 Контент отправлен на модерацию");
        }
    }
}