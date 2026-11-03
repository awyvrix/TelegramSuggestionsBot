package net.awyvrix.bot.media;

public record Media(String fileId, String uniqueId, Type type, String caption) {
    public enum Type {PHOTO, VIDEO, DOCUMENT}
}