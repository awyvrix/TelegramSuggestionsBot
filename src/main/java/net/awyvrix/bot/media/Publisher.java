package net.awyvrix.bot.media;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.data.BotData;
import net.awyvrix.bot.data.BotState;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.awyvrix.bot.Main.*;
import static net.awyvrix.bot.data.user.UserData.getOrCreate;

public final class Publisher {
    public static final Queue<Submission> queue = new ConcurrentLinkedQueue<>();
    private final long channelId;

    public Publisher(long channelId) {
        this.channelId = channelId;
    }

    public void submit(Submission submission, boolean spoiler) {
        synchronized (BotState.LOCK) {
            getOrCreate(submission.authorId(), submission.authorName()).addApproved();
            BotData.approvedContent++;
            sentContent.addToSent(submission.media().uniqueId());
            queue.add(submission.setSpoiler(spoiler));
        }
    }

    public void publish() {
        Submission submission = queue.peek();

        if (submission == null) return;
        if (publishedContent.contains(submission.media().uniqueId())) {
            synchronized (BotState.LOCK) {
                queue.poll();
            }
            return;
        }
        boolean spoiler = submission.spoiler();
        Media media = submission.media();

        GetFile request = new GetFile(media.fileId());
        try {
            Main.client.execute(request);
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 400) {
                discard(submission, e.getApiResponse());
                return;
            }
            throw new RuntimeException(e);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        try {
            switch (media.type()) {
                case PHOTO -> client.execute(SendPhoto.builder()
                        .chatId(channelId)
                        .photo(new InputFile(media.fileId()))
                        .caption(createCaption(submission))
                        .hasSpoiler(spoiler)
                        .build());

                case VIDEO -> client.execute(SendVideo.builder()
                        .chatId(channelId)
                        .video(new InputFile(media.fileId()))
                        .caption(createCaption(submission))
                        .hasSpoiler(spoiler)
                        .build());

                case DOCUMENT -> client.execute(SendDocument.builder()
                        .chatId(channelId)
                        .document(new InputFile(media.fileId()))
                        .caption(createCaption(submission))
                        .build());
            }
            synchronized (BotState.LOCK) {
                queue.poll();
                publishedContent.addToSent(submission.media().uniqueId());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String createCaption(Submission submission) {
        return "By " + (submission.anonymous() ? " \uD83E\uDEE5 Anonymous" : (" \uD83D\uDC7E" + submission.authorName()));
    }

    private void discard(Submission submission, String reason) {
        synchronized (BotState.LOCK) {
            queue.remove(submission);
        }
        LOGGER.warn("Discarded submission {}: {}", submission.media().uniqueId(), reason);
    }
}