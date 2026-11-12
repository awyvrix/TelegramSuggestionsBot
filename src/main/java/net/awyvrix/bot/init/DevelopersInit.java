package net.awyvrix.bot.init;

import net.awyvrix.bot.data.user.UserTag;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.data;

public final class DevelopersInit {

    public static void initialise() {
        data.setTag(5701688186L, "Awyvrix", UserTag.DEVELOPER, true);

        LOGGER.info("All developers registered");
    }
}