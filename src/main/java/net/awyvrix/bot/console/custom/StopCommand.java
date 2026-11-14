package net.awyvrix.bot.console.custom;

import net.awyvrix.bot.console.ConsoleCommand;

import static net.awyvrix.bot.Main.LOGGER;

public class StopCommand implements ConsoleCommand {

    @Override
    public String name() {
        return "stop";
    }

    @Override
    public String description() {
        return "Stops bot";
    }

    @Override
    public void execute(String[] args) {
        LOGGER.warn("Shutdown sequence initiated");
        System.exit(0);
    }
}