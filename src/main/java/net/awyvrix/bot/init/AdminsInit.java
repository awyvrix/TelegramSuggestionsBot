package net.awyvrix.bot.init;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.data;

public final class AdminsInit {

    static public void initialise() {
        data.addAdmin(5615120730L, "Copy68");

        LOGGER.info("All admins registered");
    }
}