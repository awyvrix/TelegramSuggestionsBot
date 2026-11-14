package net.awyvrix.bot.media;

import net.awyvrix.bot.data.BotState;
import net.awyvrix.bot.data.DataManager;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.awyvrix.bot.Main.LOGGER;

public final class Publisher {
    public static final Queue<Submission> queue = new ConcurrentLinkedQueue<>();
    private final long channelId;

    private final TelegramClient client;
    private final DataManager data;

    public Publisher(TelegramClient client, long channelId, DataManager data) {
        this.channelId = channelId;
        this.client = client;
        this.data = data;
    }

    public void submit(Submission submission, boolean spoiler) {
        synchronized (BotState.LOCK) {
            data.users().getOrCreate(submission.authorId(), submission.authorDisplayName()).addApproved();
            data.increaseApprovedContent();
            data.sentContent().addToSent(submission.media().uniqueId());
            queue.add(submission.setSpoiler(spoiler));
        }
    }

    public void publish() {
        Submission submission = queue.peek();

        if (submission == null) return;
        if (data.publishedContent().contains(submission.media().uniqueId())) {
            synchronized (BotState.LOCK) {
                queue.poll();
            }
            return;
        }
        boolean spoiler = submission.isSpoiler();
        Media media = submission.media();

        GetFile request = new GetFile(media.fileId());
        try {
            client.execute(request);
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
                data.publishedContent().addToSent(submission.media().uniqueId());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String createCaption(Submission submission) {
        return "By " + (submission.isAnonymous() ? " \uD83E\uDEE5 Anonymous" : (" \uD83D\uDC7E" + submission.authorDisplayName()));
    }

    private void discard(Submission submission, String reason) {
        synchronized (BotState.LOCK) {
            queue.remove(submission);
        }
        LOGGER.warn("Discarded submission {}: {}", submission.media().uniqueId(), reason);
    }
}