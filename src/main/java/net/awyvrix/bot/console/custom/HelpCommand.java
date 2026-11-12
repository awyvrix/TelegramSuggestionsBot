package net.awyvrix.bot.console.custom;

import net.awyvrix.bot.console.ConsoleCommand;
import net.awyvrix.bot.console.ConsoleCommandManager;

public class HelpCommand implements ConsoleCommand {
    private final ConsoleCommandManager manager;

    public HelpCommand(ConsoleCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Show available commands";
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Available commands:");
        for (ConsoleCommand cmd : manager.commands.values()) {
            System.out.println(" | " + cmd.name() + " - " + cmd.description());
        }
    }
}