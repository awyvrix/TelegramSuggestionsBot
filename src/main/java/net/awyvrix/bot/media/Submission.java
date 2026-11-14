package net.awyvrix.bot.media;

public record Submission(
        long authorId,
        long sourceChatId,
        int sourceMessageId,
        String authorDisplayName,
        boolean isAnonymous,
        boolean isSpoiler,
        Media media
) {
    public Submission setSpoiler(boolean spoiler) {
        return new Submission(authorId, sourceChatId, sourceMessageId, authorDisplayName, isAnonymous, spoiler, media);
    }
}