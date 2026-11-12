package net.awyvrix.bot.media;

public record Submission(
        long authorId,
        long chatId,
        int messageId,
        String authorName,
        boolean anonymous,
        boolean spoiler,
        Media media
) {
    public Submission setSpoiler(boolean spoiler) {
        return new Submission(authorId, chatId, messageId, authorName, anonymous, spoiler, media);
    }
}