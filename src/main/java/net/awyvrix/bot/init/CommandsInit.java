package net.awyvrix.bot.init;

import net.awyvrix.bot.Main;
import net.awyvrix.bot.commands.CommandRegistry;
import net.awyvrix.bot.commands.custom.*;
import net.awyvrix.bot.commands.custom.admin.*;

import static net.awyvrix.bot.Main.LOGGER;

public final class CommandsInit {
    private static final CommandRegistry registry = Main.commandRegistry;

    static public void initialise() {
        registry.register(new Start());
        registry.register(new Status());
        registry.register(new Troubleshooting());

        registry.register(new Maintenance());
        registry.register(new Save());
        registry.register(new Backup());
        registry.register(new AdminList());
        registry.register(new Debug());

        registry.register(new Anon());
        registry.register(new Statistics());
        registry.register(new Leaderboard());

        registry.register(new MediaProcessor());

        LOGGER.info("All commands registered");
    }
}