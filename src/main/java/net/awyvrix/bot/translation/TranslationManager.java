package net.awyvrix.bot.translation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationManager {
    private static final Map<String, Map<String, String>> LANGUAGES = new ConcurrentHashMap<>();
    private static String currentLanguage = "en_us";

    public static void loadLanguage(String language, Map<String, String> translations) {
        LANGUAGES.put(language, Map.copyOf(translations));
    }

    public static String translate(String key, Object... args) {
        String translation = LANGUAGES.getOrDefault(currentLanguage, Map.of()).get(key);

        if (translation == null)
            translation = LANGUAGES.getOrDefault("en_us", Map.of()).get(key);

        if (translation == null)
            return key;

        return args.length == 0 ? translation : String.format(translation, args);
    }

    public static void setLanguage(String language) {
        currentLanguage = language;
    }
}