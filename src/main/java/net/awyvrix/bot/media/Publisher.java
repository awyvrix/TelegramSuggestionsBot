package net.awyvrix.bot.media;

import net.awyvrix.bot.data.BotData;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

import static net.awyvrix.bot.Main.*;
import static net.awyvrix.bot.data.user.UserData.getOrCreate;
import static net.awyvrix.bot.media.PublicationScheduler.queue;

public final class Publisher {
    private final TelegramClient client;
    private final long channelId;

    public Publisher(TelegramClient client, long channelId) {
        this.client = client;
        this.channelId = channelId;
    }

    public void submit(Submission submission, boolean spoiler) {
        getOrCreate(submission.authorId(), submission.authorName()).addApproved();
        BotData.approvedContent++;
        sentContent.addToSent(submission.media().uniqueId());
        queue.add(submission.setSpoiler(spoiler));
    }

    public void publish() throws TelegramApiException, IOException {
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
        dataManager.save();
    }

    private String createCaption(Submission submission) {
        return "By " + (submission.anonymous() ? " \uD83E\uDEE5 Anonymous" : (" \uD83D\uDC7E" + submission.authorName()));
    }
}