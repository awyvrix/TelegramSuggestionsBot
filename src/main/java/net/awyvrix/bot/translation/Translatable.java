package net.awyvrix.bot.translation;

import net.awyvrix.bot.data.user.Language;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Translatable {
    private final String key;

    public Translatable(String key) {
        this.key = key;
    }

    public String get(Object... args) {
        return TranslationManager.translate(key, args);
    }
}