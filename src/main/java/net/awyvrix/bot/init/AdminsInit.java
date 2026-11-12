package net.awyvrix.bot.init;

import net.awyvrix.bot.data.user.UserTag;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.data;

public final class AdminsInit {
    public static void initialise() {
        data.setTag(5615120730L, "Copy68", UserTag.ADMIN, true);

        LOGGER.info("All admins registered");
    }
}