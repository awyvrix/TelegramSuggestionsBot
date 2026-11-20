package net.awyvrix.bot.translation;

public final class Translatable {
    private final String key;

    public Translatable(String key) {
        this.key = key;
    }

    public String get(Object... args) {
        return TranslationManager.translate(key, args);
    }
}