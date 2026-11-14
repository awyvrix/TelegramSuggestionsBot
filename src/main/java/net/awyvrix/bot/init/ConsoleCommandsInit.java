package net.awyvrix.bot.init;

import net.awyvrix.bot.console.ConsoleCommandManager;
import net.awyvrix.bot.console.custom.HelpCommand;
import net.awyvrix.bot.console.custom.ReloadConfigCommand;
import net.awyvrix.bot.console.custom.StopCommand;

import static net.awyvrix.bot.Main.LOGGER;

public final class ConsoleCommandsInit {
    public static void initialise(ConsoleCommandManager manager) {
        manager.register(new HelpCommand(manager));
        manager.register(new StopCommand());
        manager.register(new ReloadConfigCommand());

        LOGGER.info("All console commands registered");
    }
}