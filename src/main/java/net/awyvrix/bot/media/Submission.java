package net.awyvrix.bot.media;

public record Submission(
        long authorId,
        int chatId,
        String authorName,
        boolean anonymous,
        boolean spoiler,
        Media media
) {
    public Submission setSpoiler(boolean spoiler) {
        return new Submission(authorId, chatId, authorName, anonymous, spoiler, media);
    }
}