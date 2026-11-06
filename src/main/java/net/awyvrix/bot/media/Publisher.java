package net.awyvrix.bot.media;

import net.awyvrix.bot.data.BotData;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        getOrCreate(submission.authorId(), submission.authorName()).addApproved();
        BotData.approvedContent++;
        sentContent.addToSent(submission.media().uniqueId());
        queue.add(submission.setSpoiler(spoiler));
    }

    public void publish() throws TelegramApiException {
        Submission submission = queue.poll();

        if (submission == null) return;
        if (publishedContent.contains(submission.media().uniqueId())) return;
        boolean spoiler = submission.spoiler();
        Media media = submission.media();

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
        publishedContent.addToSent(submission.media().uniqueId());
    }

    private String createCaption(Submission submission) {
        return "By " + (submission.anonymous() ? " \uD83E\uDEE5 Anonymous" : (" \uD83D\uDC7E" + submission.authorName()));
    }
}