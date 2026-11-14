package net.awyvrix.bot.init;

import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;

import static net.awyvrix.bot.Main.LOGGER;

public final class AdminsInit {
    public static void initialise(UserData data) {
        data.setTag(5615120730L, "Copy68", UserTag.ADMIN, true);

        LOGGER.info("All admins registered");
    }
}