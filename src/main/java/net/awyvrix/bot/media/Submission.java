package net.awyvrix.bot.media;

public record Submission(
        long authorId,
        String authorName,
        boolean anonymous,
        boolean spoiler,
        Media media
) {
    public Submission setSpoiler(boolean spoiler) {
        return new Submission(authorId, authorName, anonymous, spoiler, media);
    }
}