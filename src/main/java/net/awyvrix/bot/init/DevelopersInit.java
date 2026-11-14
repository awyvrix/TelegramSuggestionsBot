package net.awyvrix.bot.init;

import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;

import static net.awyvrix.bot.Main.LOGGER;

public final class DevelopersInit {
    public static void initialise(UserData data) {
        data.setTag(5701688186L, "Awyvrix", UserTag.DEVELOPER, true);

        LOGGER.info("All developers registered");
    }
}