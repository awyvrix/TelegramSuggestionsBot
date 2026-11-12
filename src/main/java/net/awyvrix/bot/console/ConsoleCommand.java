package net.awyvrix.bot.console;

public interface ConsoleCommand {
    String name();
    String description();
    void execute(String[] args);
}