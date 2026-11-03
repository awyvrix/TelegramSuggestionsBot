package net.awyvrix.bot.init;

import static net.awyvrix.bot.Main.LOGGER;
import static net.awyvrix.bot.Main.data;

public final class DevelopersInit {

    static public void initialise() {
        data.addDeveloper(5701688186L, "Awyvrix");

        LOGGER.info("All developers registered");
    }
}