package net.awyvrix.bot.exceptions;

import static net.awyvrix.bot.Main.LOGGER;

public final class ErrorHandler {
    public static void report(String message, Throwable error) {
        LOGGER.error(message, error);
    }

    public static void report(String message, Throwable error, Object... context) {
        StringBuilder result = new StringBuilder(message);

        for (int i = 0; i + 1 < context.length; i += 2) {
            result.append(i == 0 ? " (" : ", ")
                  .append(context[i]).append("=")
                  .append(context[i + 1]);
        }

        if (context.length >= 2) result.append(")");
        LOGGER.error(result.toString(), error);
    }
}